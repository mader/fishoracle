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

#include "core/array_api.h"
#include "core/str_api.h"
#include "core/assert_api.h"
#include "core/ma.h"
#include "core/strand_api.h"
#include "core/ensure.h"
#include "annotationsketch/feature_collection.h"

struct GtFeatureCollection {
  GtArray *features;
};

GtFeatureCollection* gt_feature_collection_new(){
  GtFeatureCollection *fc = gt_malloc(sizeof(GtFeatureCollection));
  fc->features = gt_array_new(sizeof (GtGenomeNode*));
  return fc;
}

GtFeatureCollection* gt_feature_collection_new_from_array(GtArray *array){
  GtFeatureCollection *fc = gt_malloc(sizeof(GtFeatureCollection));
  fc->features = array;
  return fc;
}

void gt_feature_collection_add(GtFeatureCollection *fc, GtGenomeNode *gn){
  gt_array_add(fc->features, gn);
}

void gt_feature_collection_add_array(GtFeatureCollection *fc, GtArray *array){
  gt_array_add_array(fc->features, array);
}

GtGenomeNode* gt_feature_collection_get(GtFeatureCollection *fc, int index){
  return *(GtGenomeNode**) gt_array_get(fc->features, index);
}

unsigned long gt_feature_collection_size(GtFeatureCollection *fc){
  return gt_array_size(fc->features);
}

GtArray* gt_feature_collection_to_array(GtFeatureCollection *fc){
  return fc->features;
}

void gt_feature_collection_delete_contents(GtFeatureCollection *fc){
  GtGenomeNode *gn;
  int i;
  for(i = 0; i < gt_array_size(fc->features); i++){
    gn = *(GtGenomeNode**) gt_array_get(fc->features, i);
    gt_genome_node_delete(gn);
  }
}

void gt_feature_collection_delete(GtFeatureCollection *fc){
  gt_array_delete(fc->features);
  gt_free(fc);
}

int gt_feature_collection_unit_test(GtError *err){
	
  GtFeatureCollection *fc;
  GtGenomeNode *fn;
  GtStr *seqid;
  int had_err = 0;

  gt_error_check(err);

  seqid = gt_str_new_cstr("seqid");
  fn = gt_feature_node_new(seqid, "Gene", 1, 1000, GT_STRAND_FORWARD);
  gt_str_delete(seqid);
  
  fc = gt_feature_collection_new();
  
  gt_feature_collection_add(fc, fn);
  
  gt_ensure(had_err, gt_feature_collection_size(fc) == 1);
  gt_ensure(had_err, gt_genome_node_cmp(gt_feature_collection_get(fc, 0), fn) == 0);

  gt_feature_collection_delete_contents(fc);
  gt_feature_collection_delete(fc);
  
  return had_err;
  
}
