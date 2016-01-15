#! /usr/local/bin/python3
# coding = utf-8

import email
import smtplib
import email.mime.multipart
import email.mime.text

__author__ = 'jacksu'




def send_mail(from_list, to_list, sub, content):
    msg = email.mime.multipart.MIMEMultipart()
    msg['from'] = from_list
    msg['to'] = to_list
    msg['subject'] = sub
    content = content
    txt = email.mime.text.MIMEText(content)
    msg.attach(txt)

    smtp = smtplib.SMTP('localhost')
    smtp.sendmail(from_list, to_list, str(msg))
    smtp.quit()