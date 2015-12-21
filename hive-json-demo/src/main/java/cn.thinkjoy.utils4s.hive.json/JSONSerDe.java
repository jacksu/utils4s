package cn.thinkjoy.utils4s.hive.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.SerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This SerDe can be used for processing JSON data in Hive. It supports
 * arbitrary JSON data, and can handle all Hive types except for UNION. However,
 * the JSON data is expected to be a series of discrete records, rather than a
 * JSON array of objects.
 *
 * The Hive table is expected to contain columns with names corresponding to
 * fields in the JSON data, but it is not necessary for every JSON field to have
 * a corresponding Hive column. Those JSON fields will be ignored during
 * queries.
 *
 * Example:
 *
 * { "a": 1, "b": [ "str1", "str2" ], "c": { "field1": "val1" } }
 *
 * Could correspond to a table:
 *
 * CREATE TABLE foo (a INT, b ARRAY<STRING>, c STRUCT<field1:STRING>);
 *
 * JSON objects can also interpreted as a Hive MAP type, so long as the keys and
 * values in the JSON object are all of the appropriate types. For example, in
 * the JSON above, another valid table declaraction would be:
 *
 * CREATE TABLE foo (a INT, b ARRAY<STRING>, c MAP<STRING,STRING>);
 *
 * Only STRING keys are supported for Hive MAPs.
 */
public class JSONSerDe implements SerDe {

    private StructTypeInfo rowTypeInfo;
    private ObjectInspector rowOI;
    private List<String> colNames;
    private List<Object> row = new ArrayList<Object>();

    //遇到非JSON格式输入的时候的处理。
    private boolean ignoreInvalidInput;

    /**
     * An initialization function used to gather information about the table.
     * Typically, a SerDe implementation will be interested in the list of
     * column names and their types. That information will be used to help
     * perform actual serialization and deserialization of data.
     */
    @Override
    public void initialize(Configuration conf, Properties tbl)
            throws SerDeException {
        // 遇到无法转换成JSON对象的字符串时，是否忽略，默认不忽略，抛出异常，设置为true将跳过异常。
        ignoreInvalidInput = Boolean.valueOf(tbl.getProperty(
                "input.invalid.ignore", "false"));

        // Get a list of the table's column names.

        String colNamesStr = tbl.getProperty(serdeConstants.LIST_COLUMNS);
        colNames = Arrays.asList(colNamesStr.split(","));

        // Get a list of TypeInfos for the columns. This list lines up with
        // the list of column names.
        String colTypesStr = tbl.getProperty(serdeConstants.LIST_COLUMN_TYPES);
        List<TypeInfo> colTypes = TypeInfoUtils
                .getTypeInfosFromTypeString(colTypesStr);

        rowTypeInfo = (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(
                colNames, colTypes);
        rowOI = TypeInfoUtils
                .getStandardJavaObjectInspectorFromTypeInfo(rowTypeInfo);
    }

    /**
     * This method does the work of deserializing a record into Java objects
     * that Hive can work with via the ObjectInspector interface. For this
     * SerDe, the blob that is passed in is a JSON string, and the Jackson JSON
     * parser is being used to translate the string into Java objects.
     *
     * The JSON deserialization works by taking the column names in the Hive
     * table, and looking up those fields in the parsed JSON object. If the
     * value of the field is not a primitive, the object is parsed further.
     */
    @Override
    public Object deserialize(Writable blob) throws SerDeException {
        Map<?, ?> root = null;
        row.clear();
        try {
            ObjectMapper mapper = new ObjectMapper();
            // This is really a Map<String, Object>. For more information about
            // how
            // Jackson parses JSON in this example, see
            // http://wiki.fasterxml.com/JacksonDataBinding
            root = mapper.readValue(blob.toString(), Map.class);
        } catch (Exception e) {
            // 如果为true,不抛出异常，忽略该行数据
            if (!ignoreInvalidInput)
                throw new SerDeException(e);
            else {
                return null;
            }

        }

        // Lowercase the keys as expected by hive
        Map<String, Object> lowerRoot = new HashMap();
        for (Map.Entry entry : root.entrySet()) {
            lowerRoot.put(((String) entry.getKey()).toLowerCase(),
                    entry.getValue());
        }
        root = lowerRoot;

        Object value = null;
        for (String fieldName : rowTypeInfo.getAllStructFieldNames()) {
            try {
                TypeInfo fieldTypeInfo = rowTypeInfo
                        .getStructFieldTypeInfo(fieldName);
                value = parseField(root.get(fieldName), fieldTypeInfo);
            } catch (Exception e) {
                value = null;
            }
            row.add(value);
        }
        return row;
    }

    /**
     * Parses a JSON object according to the Hive column's type.
     *
     * @param field
     *            - The JSON object to parse
     * @param fieldTypeInfo
     *            - Metadata about the Hive column
     * @return - The parsed value of the field
     */
    private Object parseField(Object field, TypeInfo fieldTypeInfo) {
        switch (fieldTypeInfo.getCategory()) {
            case PRIMITIVE:
                // Jackson will return the right thing in this case, so just return
                // the object
                if (field instanceof String) {
                    field = field.toString().replaceAll("\n", "\\\\n");
                }
                return field;
            case LIST:
                return parseList(field, (ListTypeInfo) fieldTypeInfo);
            case MAP:
                return parseMap(field, (MapTypeInfo) fieldTypeInfo);
            case STRUCT:
                return parseStruct(field, (StructTypeInfo) fieldTypeInfo);
            case UNION:
                // Unsupported by JSON
            default:
                return null;
        }
    }

    /**
     * Parses a JSON object and its fields. The Hive metadata is used to
     * determine how to parse the object fields.
     *
     * @param field
     *            - The JSON object to parse
     * @param fieldTypeInfo
     *            - Metadata about the Hive column
     * @return - A map representing the object and its fields
     */
    private Object parseStruct(Object field, StructTypeInfo fieldTypeInfo) {
        Map<Object, Object> map = (Map<Object, Object>) field;
        ArrayList<TypeInfo> structTypes = fieldTypeInfo
                .getAllStructFieldTypeInfos();
        ArrayList<String> structNames = fieldTypeInfo.getAllStructFieldNames();

        List<Object> structRow = new ArrayList<Object>(structTypes.size());
        for (int i = 0; i < structNames.size(); i++) {
            structRow.add(parseField(map.get(structNames.get(i)),
                    structTypes.get(i)));
        }
        return structRow;
    }

    /**
     * Parse a JSON list and its elements. This uses the Hive metadata for the
     * list elements to determine how to parse the elements.
     *
     * @param field
     *            - The JSON list to parse
     * @param fieldTypeInfo
     *            - Metadata about the Hive column
     * @return - A list of the parsed elements
     */
    private Object parseList(Object field, ListTypeInfo fieldTypeInfo) {
        ArrayList<Object> list = (ArrayList<Object>) field;
        TypeInfo elemTypeInfo = fieldTypeInfo.getListElementTypeInfo();

        for (int i = 0; i < list.size(); i++) {
            list.set(i, parseField(list.get(i), elemTypeInfo));
        }

        return list.toArray();
    }

    /**
     * Parse a JSON object as a map. This uses the Hive metadata for the map
     * values to determine how to parse the values. The map is assumed to have a
     * string for a key.
     *
     * @param field
     *            - The JSON list to parse
     * @param fieldTypeInfo
     *            - Metadata about the Hive column
     * @return
     */
    private Object parseMap(Object field, MapTypeInfo fieldTypeInfo) {
        Map<Object, Object> map = (Map<Object, Object>) field;
        TypeInfo valueTypeInfo = fieldTypeInfo.getMapValueTypeInfo();

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            map.put(entry.getKey(), parseField(entry.getValue(), valueTypeInfo));
        }
        return map;
    }

    /**
     * Return an ObjectInspector for the row of data
     */
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return rowOI;
    }

    /**
     * Unimplemented
     */
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }

    /**
     * JSON is just a textual representation, so our serialized class is just
     * Text.
     */
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return Text.class;
    }

    /**
     * This method takes an object representing a row of data from Hive, and
     * uses the ObjectInspector to get the data for each column and serialize
     * it. This implementation deparses the row into an object that Jackson can
     * easily serialize into a JSON blob.
     */
    @Override
    public Writable serialize(Object obj, ObjectInspector oi)
            throws SerDeException {
        Object deparsedObj = deparseRow(obj, oi);
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Let Jackson do the work of serializing the object
            return new Text(mapper.writeValueAsString(deparsedObj));
        } catch (Exception e) {
            throw new SerDeException(e);
        }
    }

    /**
     * Deparse a Hive object into a Jackson-serializable object. This uses the
     * ObjectInspector to extract the column data.
     *
     * @param obj
     *            - Hive object to deparse
     * @param oi
     *            - ObjectInspector for the object
     * @return - A deparsed object
     */
    private Object deparseObject(Object obj, ObjectInspector oi) {
        switch (oi.getCategory()) {
            case LIST:
                return deparseList(obj, (ListObjectInspector) oi);
            case MAP:
                return deparseMap(obj, (MapObjectInspector) oi);
            case PRIMITIVE:
                return deparsePrimitive(obj, (PrimitiveObjectInspector) oi);
            case STRUCT:
                return deparseStruct(obj, (StructObjectInspector) oi, false);
            case UNION:
                // Unsupported by JSON
            default:
                return null;
        }
    }

    /**
     * Deparses a row of data. We have to treat this one differently from other
     * structs, because the field names for the root object do not match the
     * column names for the Hive table.
     *
     * @param obj
     *            - Object representing the top-level row
     * @param structOI
     *            - ObjectInspector for the row
     * @return - A deparsed row of data
     */
    private Object deparseRow(Object obj, ObjectInspector structOI) {
        return deparseStruct(obj, (StructObjectInspector) structOI, true);
    }

    /**
     * Deparses struct data into a serializable JSON object.
     *
     * @param obj
     *            - Hive struct data
     * @param structOI
     *            - ObjectInspector for the struct
     * @param isRow
     *            - Whether or not this struct represents a top-level row
     * @return - A deparsed struct
     */
    private Object deparseStruct(Object obj, StructObjectInspector structOI,
                                 boolean isRow) {
        Map<Object, Object> struct = new HashMap<Object, Object>();
        List<? extends StructField> fields = structOI.getAllStructFieldRefs();
        for (int i = 0; i < fields.size(); i++) {
            StructField field = fields.get(i);
            // The top-level row object is treated slightly differently from
            // other
            // structs, because the field names for the row do not correctly
            // reflect
            // the Hive column names. For lower-level structs, we can get the
            // field
            // name from the associated StructField object.
            String fieldName = isRow ? colNames.get(i) : field.getFieldName();
            ObjectInspector fieldOI = field.getFieldObjectInspector();
            Object fieldObj = structOI.getStructFieldData(obj, field);
            struct.put(fieldName, deparseObject(fieldObj, fieldOI));
        }
        return struct;
    }

    /**
     * Deparses a primitive type.
     *
     * @param obj
     *            - Hive object to deparse
     * @param primOI
     *            - ObjectInspector for the object
     * @return - A deparsed object
     */
    private Object deparsePrimitive(Object obj, PrimitiveObjectInspector primOI) {
        return primOI.getPrimitiveJavaObject(obj);
    }

    private Object deparseMap(Object obj, MapObjectInspector mapOI) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        ObjectInspector mapValOI = mapOI.getMapValueObjectInspector();
        Map<?, ?> fields = mapOI.getMap(obj);
        for (Map.Entry<?, ?> field : fields.entrySet()) {
            Object fieldName = field.getKey();
            Object fieldObj = field.getValue();
            map.put(fieldName, deparseObject(fieldObj, mapValOI));
        }
        return map;
    }

    /**
     * Deparses a list and its elements.
     *
     * @param obj
     *            - Hive object to deparse
     * @param listOI
     *            - ObjectInspector for the object
     * @return - A deparsed object
     */
    private Object deparseList(Object obj, ListObjectInspector listOI) {
        List<Object> list = new ArrayList<Object>();
        List<?> field = listOI.getList(obj);
        ObjectInspector elemOI = listOI.getListElementObjectInspector();
        for (Object elem : field) {
            list.add(deparseObject(elem, elemOI));
        }
        return list;
    }
}