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

# get previous messages
f = open("messages", "r")
messages = f.read() + "<br />" + msg + "<br />"
f.close()

# write new message
f = open("messages", "w")
f.write(messages)
f.close()

#another = 1
print "<html>" 
print "<div align=\"center\">"
print "<h1><u>Ze Vall</u></h1>"
print messages 
print "<br/>"
print "Cool story bro, say it again! (yes or no)"

# ask user if he wants to leave another message
print "<form name=\"getanother\" action=\"http://www.cs.mcgill.ca/~fkhan24/cgi-bin/redirect.py\" method=\"POST\">"
print "<input type=\"input\" name=\"another\" value=\"\">"
print "<input type=\"submit\" value=\"OK\"/>"
print "</form>"

print "</div>"
print "</html>"  