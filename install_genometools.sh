#!/bin/sh

git clone https://github.com/genometools/genometools.git

cp -R gt_db_api/* genometools/

cd genometools/

if test `uname -m` = "x86_64"; then
	make -j 64bit=yes with-mysql=yes;
fi

if test `uname -m` = "i386"; then
	make -j with-mysql=yes;
fi
