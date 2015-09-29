#!/bin/python

import sys 
import feedparser
import argparse

for arg in sys.argv[1:]:
	d = feedparser.parse (arg)
	print(d['feed']['title'])
