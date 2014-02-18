#!/bin/sh

git clone https://github.com/genometools/genometools.git

cd genometools

git checkout -b v1.5.2 v1.5.2

cd ..

cp -R gt_db_api/* genometools/

cd genometools/

if test `uname -m` = "x86_64"; then
	make 64bit=yes with-mysql=yes;
fi

if test `uname -m` = "i386"; then
	make with-mysql=yes;
fi
