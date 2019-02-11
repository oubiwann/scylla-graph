#!/bin/bash

DATA_SET_URL=http://files.grouplens.org/datasets/movielens/ml-1m.zip
ZIP_FILE=$(basename $DATA_SET_URL)
IMPORT_DIR=sample-data
mkdir -p $IMPORT_DIR
curl -o ${IMPORT_DIR}/$ZIP_FILE $DATA_SET_URL
cd $IMPORT_DIR && \
	unzip $ZIP_FILE
