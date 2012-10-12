#!/usr/bin/python
import cgi, cgitb 
import sys

print "Content-type:text/html\r\n\r\n"

# Create instance of FieldStorage 
form = cgi.FieldStorage() 
another = ""

# get data from field
if form.getvalue("another"):
	another = form.getvalue("another")

if another == "no":
	print "<html> <center> <h1>See you later</h1> <img src=\"alligator.jpg\" alt=\"Smiley face\" height=\"200\" width=\"200\"> </center> </html>"
else:
	print "<html>"
	print "<body>"
	print "<div align=\"center\">"
	print "<h1> <q>If you can express your soul, the rest ceases to matter. </q></h1>"
	print "<br/>"
	print "<h3>"
	print "<img src=\"arnold.jpg\" alt=\"Smiley face\" height=\"200\" width=\"200\">"
	print "<br/>COME AT ME BRO, ARGH!<br/>"
	print "<form name=\"getmsg\" action=\"http://www.cs.mcgill.ca/~fkhan24/cgi-bin/wall.py\" method=\"POST\">"
	print "<input type=\"input\" name=\"userMsg\" value=\"\">"
	print "<input type=\"submit\" value=\"Submit Message\"/>"
	print "</form>"
	print "</h3>"
	print "</div>"
	print "</body>"
	print "</html>"