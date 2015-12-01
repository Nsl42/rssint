#!/bin/python2

import os
import pylucene
import feedparser
import hashlib
import parser
import readability
from sys import argv,exit
from readability import Document
import urllib
from urllib2 import URLError
from multiprocessing import Pool

ITEMLOC = '/tmp/rssint/'
UPDATEMODE = 'lazy'
#UPDATEMODE = 'none'

def content(link):
    target = urllib.urlopen(link)
    d = Document(input=target)
    #Catching if not u''
    return d.summary()

def writer(entry):
    #Using the sha256 over the description to generate the item's id
    id = hashlib.sha256(entry.description.encode('utf-8')).hexdigest()
    if(os.path.exists(ITEMLOC + id) and UPDATEMODE == 'lazy'):
        return False
    with open(ITEMLOC + id, 'w') as f:
        f.write('Title: %s\n' % entry.title.encode('utf-8'))
        f.write('Date: %s\n' % entry.published)
        f.write('Link: %s\n' % entry.link)
        f.write('Feed: %s\n' % entry.feedlink)
        f.write('Desc: %s\n' % entry.description.encode('utf-8'))
        f.write('\n')
        f.write(content(entry.link).encode('utf-8'))
        f.write('\n')
    return True

def spyder(url):
    pool = Pool(processes=4)
    print 'parsing %s...' % url
    try:
        d = feedparser.parse(url)
    except URLError:
        print 'ERROR: The network could not be found. Check your internet connection and try again.'
        exit(-1)
    for ent in d.entries:
        ent.feedlink = d.feed.link
    res = pool.map(writer, tuple(d.entries))
    i = 0
    for r in res:
        if r: i += 1
    print '%s\t%d/%d element(s) written' % (d.feed.title, i, len(d.entries))


# MAIN

if len(argv) == 1:
    print "usage : ./phelps.py filename"

if not os.path.exists(ITEMLOC) :
    os.makedirs(ITEMLOC)

for arg in argv[1:]:
    with open(arg) as f:
        for line in f:
            try:
                spyder(line)
            except AttributeError:
                print 'WARNING: Malformed rss feed; Skipping this one...'


