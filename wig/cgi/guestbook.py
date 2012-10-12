#!/usr/bin/python

import cgi, cgitb 

import sys
print "Content-type:text/html\r\n\r\n"

# Create instance of FieldStorage 
form = cgi.FieldStorage() 
msg=""

# get data from field

if form.getvalue("userMsg"):
	msg = form.getvalue("userMsg")

f = open("messages", "r")
messages = f.read() + "<br />" + msg + "<br />"
f.close()
f = open("messages", "w")
f.write(messages)
f.close()

print "<html><body>" 

print "<h1>The Anonymous Wall</h1>"

print messages 

print "Cool story bro, say it again? (yes/no)"
