#!/bin/python2

import sys 
import feedparser

def spyder(url):
    d = feedparser.parse(url)
    print('title:\t%s' % d.feed.title)
    print('link:\t%s' % d.feed.link)
    print('desc:\t%s' % d.feed.description)
    # print('date:\t%s' % d.feed.published)
    print()
    for l in d.entries:
        print(' title:\t%s' % l.title)
        print(' link:\t%s' % l.link)
        print(' desc:\t%s' % l.description)
        print(' date:\t%s' % l.published)
        print(' id:\t%s' % l.id)
    print()
    print()


for arg in sys.argv[1:]:
    with open(arg) as f:
        for line in f:
            spyder(line)
