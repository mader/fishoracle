/*
  Copyright (c) 2011 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2011 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

#ifndef FEATURE_COLLECTION_API_H
#define FEATURE_COLLECTION_API_H

#include "extended/genome_node_api.h"

/* The <GtFeatureCollection> represents a container to hold <GtGenomeNode>
   objects. It wraps <GtArray> to circumvent the use of <GtArray> 
   within the language bindings. */
typedef struct GtFeatureCollection GtFeatureCollection;

GtFeatureCollection* gt_feature_collection_new();
GtFeatureCollection* gt_feature_collection_new_from_array(GtArray *array);
void                   gt_feature_collection_add(GtFeatureCollection *fc,
                                                           GtGenomeNode *gn);
void gt_feature_collection_add_array(GtFeatureCollection *fc, GtArray *array);
GtGenomeNode*           gt_feature_collection_get(GtFeatureCollection *fc, int index);
unsigned long         gt_feature_collection_size(GtFeatureCollection *fc);
/* Delivers a pointer to the underlying <GtArray> object. */
GtArray*               gt_feature_collection_to_array(GtFeatureCollection *fc);
void                   gt_feature_collection_delete_contents(GtFeatureCollection *fc);
void                   gt_feature_collection_delete(GtFeatureCollection *fc);

#endif
