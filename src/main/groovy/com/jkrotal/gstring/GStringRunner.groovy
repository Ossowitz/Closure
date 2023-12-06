package com.jkrotal.gstring

/**
 * " "
 * ' '
 * """ """
 * ''' '''
 * / /
 * $/ /$
 */

char c = 'H'

String ilya = "Ilya"
String name = "Ilya 'Hi!'"
String name2 = "Ilya \""
String value = 'Hello "Hi!"'
String stringValue = "Hello $ilya"
String nameValue = 'Hello $value'

GString value5 = /Hello world $ilya/
String value6 = $/Hello world/$


String query = """
SELECT * 
FROM table 
WHERE name = ${getWithPrefix(ilya)}
"""

String query2 = '''
SELECT * 
FROM table 
WHERE name = ${ilya}
'''

def getWithPrefix(String name) {
    "prefix-" + name
}

char a = ilya[2]
char b = ilya[-1]
String d = ilya[1..2]
String string = ilya * 3
String il = ilya - 'ya'