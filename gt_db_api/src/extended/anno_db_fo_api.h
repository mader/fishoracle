/*
  Copyright (c) 2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012 Center for Bioinformatics, University of Hamburg

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

#ifndef ANNO_DB_FO_API_H
#define ANNO_DB_FO_API_H

#include "core/error_api.h"
#include "extended/anno_db_schema_api.h"
#include "extended/feature_index_rep.h"
#include "annotationsketch/block_api.h"
#include "annotationsketch/layout.h"

/* Implements the <GtAnnoDBSchema> interface. */
typedef struct GtAnnoDBFo GtAnnoDBFo;

typedef struct GtFeatureIndexFo GtFeatureIndexFo;

typedef enum {
  SEGMENT,
  MUTATION,
  TRANSLOCATION,
  GENERIC
}GtFeatureIndexFeatureType;

typedef enum {
  INTENSITY,
  STATUS
}GtSegmentType;

typedef enum {
  INT,
  DOUBLE,
  STR
}FilterType;

#define feature_index_fo_cast(V)\
        gt_feature_index_cast(feature_index_fo_class(), V)

const GtFeatureIndexClass* feature_index_fo_class(void);

GtAnnoDBSchema* gt_anno_db_fo_new(void);

void  gt_feature_index_fo_filter_segment_only(GtFeatureIndexFo *fi,
                                               GtSegmentType type);
void  gt_feature_index_fo_filter_mutations_only(GtFeatureIndexFo *fi);
void  gt_feature_index_fo_filter_translocations_only(GtFeatureIndexFo *fi);
void  gt_feature_index_fo_filter_generic_only(GtFeatureIndexFo *fi);
void  gt_feature_index_fo_reset_filter_type(GtFeatureIndexFo *fi);

void  gt_feature_index_fo_unset_all_filters(GtFeatureIndexFo *fi);

void gt_feature_index_fo_set_segments_th(GtFeatureIndexFo *fi,
                                               double lower_th);
void gt_feature_index_fo_unset_segments_th(GtFeatureIndexFo *fi);

void gt_feature_index_fo_set_track_id(GtFeatureIndexFo *fi,
                                      const char *track_id);
void gt_feature_index_fo_unset_track_id(GtFeatureIndexFo *fi);

void gt_feature_index_fo_set_segments_sorted(GtFeatureIndexFo *fi,
                                             bool sorted);

void gt_feature_index_fo_set_score(GtFeatureIndexFo *fi,
                                   double score,
                                   bool grater_than);

void gt_feature_index_fo_unset_score(GtFeatureIndexFo *fi);

void gt_feature_index_fo_add_where_clause_int_filter(GtFeatureIndexFo *fi,
                                                     char *column,
                                                     int filter[],
                                                     int length);
void gt_feature_index_fo_add_where_clause_str_filter(GtFeatureIndexFo *fi,
                                                     char *column,
                                                     char* filter[],
                                                     int length);
void gt_feature_index_fo_add_where_clause_double_filter(GtFeatureIndexFo *fi,
                                                        char *column,
                                                        double filter[],
                                                        int length);

void gt_feature_index_fo_reset_where_clause_int_filter(GtFeatureIndexFo *fi);
void gt_feature_index_fo_reset_where_clause_str_filter(GtFeatureIndexFo *fi);
void gt_feature_index_fo_reset_where_clause_double_filter(GtFeatureIndexFo *fi);

void gt_feature_index_fo_add_segment_status_filter(GtFeatureIndexFo *fi,
										           int filter[],
												   int length);
void gt_feature_index_fo_add_generic_filter(GtFeatureIndexFo *fi,
												char* filter[],
												int length);
void gt_feature_index_fo_add_project_filter(GtFeatureIndexFo *fi,
                                            int filter[],
                                            int length);
void gt_feature_index_fo_add_tissue_filter(GtFeatureIndexFo *fi,
                                           int filter[],
                                           int length);

void gt_feature_index_fo_add_somatic_filter(GtFeatureIndexFo *fi,
                                            char* filter[],
                                            int length);
void gt_feature_index_fo_add_confidence_filter(GtFeatureIndexFo *fi,
                                               char* filter[],
                                               int length);
void gt_feature_index_fo_add_snptool_filter(GtFeatureIndexFo *fi,
                                            char* filter[],
                                            int length);

void gt_feature_index_fo_set_additional_experiment_filter(GtFeatureIndexFo *fi,
                                                          int filter[],
                                                          int length);
                                                          
void gt_feature_index_fo_unset_additional_experiment_filter(
                                                        GtFeatureIndexFo *fi);

void gt_feature_index_fo_set_location(GtFeatureIndexFo *fi, 
                                       char *seqid, 
                                       GtRange *range);

int gt_feature_index_fo_get_features(GtFeatureIndexFo *fi,
                                     GtArray *results,
                                     GtError *err);

int gt_feature_index_fo_sort_segments_for_coverage(GtArray *segments);
            
int gt_feature_index_fo_process_mutations(GtArray *results,
                                          GtArray *mutations,
                                          GtRDB *rdb,
                                          const char *track_id,
                                          char* biotype_filter[],
                                          int b_length,
                                          GtError *err);

int gt_feature_index_fo_process_translocations(GtFeatureIndexFo *fi,
                                      GtArray *translocations,
                                      GtRDB *rdb,
                                      char *track_id,
                                      char* biotype_filter[],
                                      int b_length,
                                      GtError *err);
                                      
void set_layout_block_sort(GtLayout *l);
int sort_blocks(const GtBlock *b1, const GtBlock *b2, void *data);

int gt_anno_db_fo_unit_test(GtError *err);

#endif
