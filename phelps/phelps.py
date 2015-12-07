#!/bin/python2

import os
import time
import feedparser
import hashlib
import parser
import readability
from readability import Document
from asciinator import html2text
from sys import argv, exit
import urllib
from urllib2 import URLError
from multiprocessing import Pool

ITEMLOC = '/tmp/rssint/'
UPDATEMODE = 'lazy'
#UPDATEMODE = 'none'

def content(link):
    target = urllib.urlopen(link)
    d = Document(input=target)
    # catching if not u''
    return d.summary()

def writer(entry):
    # using the sha256 over the description to generate the item's id
    id = hashlib.sha256(entry.description.encode('utf-8')).hexdigest()
    desc = content(entry.link)
    if not desc:
        desc = entry.description
    if not entry.category:
        entry.category = '\n';
    if os.path.exists(ITEMLOC + id) and UPDATEMODE == 'lazy':
        return False
    with open(ITEMLOC + id, 'w') as f:
        f.write('Feed: %s\n' % entry.feedlink)
        f.write('Category: %s' % entry.category)
        f.write('Title: %s\n' % entry.title.encode('utf-8'))
        f.write('Date: %s\n' % entry.published)
        f.write('DateProc: %d\n' % time.time())
        f.write('Link: %s\n' % entry.link)
        f.write('\n')
        f.write(html2text(desc).encode('utf-8'))
        f.write('\n')
    return True

def spyder(url, category):
    pool = Pool(processes=4)
    print 'parsing %s...' % url
    try:
        d = feedparser.parse(url)
    except URLError:
        print 'ERROR: The network could not be found. Check your internet connection and try again.'
        exit(-1)
    for ent in d.entries:
        ent.feedlink = d.feed.link
        ent.category = category
    res = pool.map(writer, tuple(d.entries))
    i = 0
    for r in res:
        if r: i += 1
    print '%s\t%d/%d element(s) written' % (d.feed.title, i, len(d.entries))


# MAIN

if len(argv) == 1:
    print "usage : ./phelps.py filename..."

if not os.path.exists(ITEMLOC) :
    os.makedirs(ITEMLOC)

for arg in argv[1:]:
    with open(arg) as f:
        for line in f:
            entry = line.split('\t')
            try:
                spyder(entry[0], ', '.join(entry[1:]))
            except AttributeError:
                print 'WARNING: Malformed rss feed; Skipping this one...'


