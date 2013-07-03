#!/bin/sh

wget http://www.genometools.org/pub/genometools-unstable.tar.gz

tar -xvzf genometools-unstable.tar.gz

cp -R gt_db_api/* genometools-unstable/

cd genometools-unstable/

if test `uname -m` = "x86_64"; then
	make -j 64bit=yes with-mysql=yes;
fi

if test `uname -m` = "i386"; then
	make -j with-mysql=yes;
fi
