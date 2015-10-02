#!/bin/python2

import sys 
import feedparser
import hashlib

ITEMLOC = '/tmp/rssint/'

def writer(id, entry, feedlink):
    with open(ITEMLOC + id, 'w') as f:
        f.write('Title: %s\n' % entry.title.encode('utf-8'))
        f.write('Date: %s\n' % entry.published)
        f.write('Link: %s\n' % entry.link)
        f.write('Feed: %s\n' % feedlink)
        f.write('\n')
        f.write(entry.description.encode('utf-8'))
        f.write('\n')

def spyder(url):
    d = feedparser.parse(url)
    print('title:\t%s' % d.feed.title)
    print('link:\t%s' % d.feed.link)
    print('desc:\t%s' % d.feed.description)
    #print('date:\t%s' % d.feed.published)
    print
    for l in d.entries:
        #Generating the Id : SHA1 over l.description
        key = hashlib.sha256(l.description.encode('utf-8'))
        writer(key.hexdigest(), l, d.feed.link)
    print
    print


for arg in sys.argv[1:]:
    with open(arg) as f:
        for line in f:
            spyder(line)
