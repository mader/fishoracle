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

#include <string.h>
#include <stdlib.h>
#include "core/assert_api.h"
#include "extended/anno_db_fo_api.h"
#include "extended/anno_db_prepared_stmt.h"
#include "extended/anno_db_schema_rep.h"
#include "extended/rdb_visitor_rep.h"
#include "extended/feature_index_rep.h"
#include "extended/feature_index.h"
#include "extended/feature_visitor.h"
#include "core/cstr_api.h"
#include "core/str_array_api.h"
#include "core/ensure.h"
#include "core/hashmap_api.h"
#include "core/ma.h"
#include "core/range.h"
#include "core/strand_api.h"
#include "core/thread_api.h"
#include "core/fa.h"
#include "extended/genome_node.h"
#include "extended/feature_node.h"
#include "extended/feature_node_iterator_api.h"
#include "extended/sql_base_query_info_api.h"
#include "extended/anno_db_ensembl_api.h"
#include "extended/anno_db_ensembl_gene_adaptor_api.h"

#define ID "ID"
#define NAME "Name"
#define STUDY_ID "study_id"
#define TYPE "type"
#define FEATURE_TYPE "feature_type"
#define NOF_MUTATIONS "nof_mutations"
#define TRANSLOC_REF_ID "transloc_ref_id"

struct GtAnnoDBFo {
  const GtAnnoDBSchema parent_instance;
  GtRDB *db;
  GtRDBVisitor *visitor;
};

typedef struct {
  const GtRDBVisitor parent_instance;
  GtAnnoDBFo *annodb;
} FoSetupVisitor;

struct GtFeatureIndexFo {
  const GtFeatureIndex parent_instance;
  GtRDB *db;
  GtFeatureIndexFeatureType feature_type;
  GtBaseQueryInfo *bqi;
  GtHashmap *where_clause_filter_int;
  GtHashmap *where_clause_filter_double;
  GtHashmap *where_clause_filter_str;
  GtArray *additional_experiments;
  GtSegmentType segment_type;
  double segment_th;
  GtStr *track_id;
  bool sorted;
  double score;
  GtStr* seqid;
  GtRange *range;
  bool greater_than_score;
  GtMutex *dblock;
  bool transaction_lock;
};

const GtAnnoDBSchemaClass* gt_anno_db_fo_class(void);
static const GtRDBVisitorClass* fo_setup_visitor_class(void);

GtStr* get_filter_SQL_where_clause_str(GtStr *column, GtArray *filter);
GtStr* get_filter_SQL_where_clause_int(GtStr *column, GtArray *filter);
GtStr* get_filter_SQL_where_clause_double(GtStr *column, GtArray *filter);
void init_adaptor(GtFeatureIndexFo *fi);
GtStr* get_filter_SQL_where_clause(GtStr *column,
                                   GtArray *filter,
                                   FilterType type);

#define anno_db_fo_cast(V)\
        gt_anno_db_schema_cast(gt_anno_db_fo_class(), V)

#define fo_setup_visitor_cast(V)\
        gt_rdb_visitor_cast(fo_setup_visitor_class(), V)

void anno_db_fo_free(GtAnnoDBSchema *s)
{
  GtAnnoDBFo *ade = anno_db_fo_cast(s);
  gt_rdb_visitor_delete(ade->visitor);
}

int gt_feature_index_fo_add_region_node()
{
  return 0;
}

int gt_feature_index_fo_add_feature_node()
{
  return 0;
}

int gt_feature_index_fo_remove_node()
{
  return 0;
}

GtArray* gt_feature_index_fo_get_features_for_seqid()
{
  return NULL;
}

int gt_feature_index_fo_get_features_for_range()
{
  return 0;
}

char* gt_feature_index_fo_get_first_seqid()
{
  return NULL;
}

GtStrArray* gt_feature_index_fo_get_seqids()
{
  return NULL;
}

int gt_feature_index_fo_get_orig_range_for_seqid()
{
  return 0;
}

int gt_feature_index_fo_get_range_for_seqid()
{
  return 0;
}

int gt_feature_index_fo_has_seqid()
{
  return 0;
}

int gt_feature_index_fo_save()
{
  return 0;
}

//TODO refactor: reduce duplicate code...
int hash_iterate_str(void *key, void *value, void *data,
                                  GtError *err){
  gt_error_check(err);
  GtStrArray *qry = (GtStrArray*) data;
  
  GtStr *gt_key = gt_str_new_cstr((char*) key);
  
  GtStr *q = get_filter_SQL_where_clause_str(gt_key, value);
  
  gt_str_array_add(qry, q);
  
  gt_str_delete(q);
  gt_str_delete(gt_key);
  
  return 0;
}

int hash_iterate_int(void *key, void *value, void *data,
                                  GtError *err){
  gt_error_check(err);
  GtStrArray *qry = (GtStrArray*) data;
  
  GtStr *gt_key = gt_str_new_cstr((char*) key);
  
  GtStr *q = get_filter_SQL_where_clause_int(gt_key, value);
  
  gt_str_array_add(qry, q);
  
  gt_str_delete(q);
  gt_str_delete(gt_key);
  
  return 0;
}

int hash_iterate_double(void *key, void *value, void *data,
                                  GtError *err){
  gt_error_check(err);
  GtStrArray *qry = (GtStrArray*) data;
  
  GtStr *gt_key = gt_str_new_cstr((char*) key);
  
  GtStr *q = get_filter_SQL_where_clause_double(gt_key, value);
  
  gt_str_array_add(qry, q);
  
  gt_str_delete(q);
  gt_str_delete(gt_key);
  
  return 0;
}

static GtArray* int_array_to_gt_array(int arr[], int length)
{
    GtArray *gt_arr = gt_array_new(sizeof(int));
  
    int i;
  
    for(i = 0; i < length; i++)
    {
      gt_array_add(gt_arr, arr[i]);
    }
  
  return gt_arr;
}

static GtArray* double_array_to_gt_array(double arr[], int length)
{
    GtArray *gt_arr = gt_array_new(sizeof(double));
  
    int i;
  
    for(i = 0; i < length; i++)
    {
      gt_array_add(gt_arr, arr[i]);
    }
  
  return gt_arr;
}

static GtArray* char_array_to_gt_array(char* arr[], int length)
{
  
    GtArray *gt_arr = gt_array_new(sizeof(char*));
  
    int i;
  
    for(i = 0; i < length; i++)
    {
      gt_array_add(gt_arr, arr[i]);
    }
  
  return gt_arr;
}

void  gt_feature_index_fo_filter_segment_only(GtFeatureIndexFo *fi,
                                               GtSegmentType type){
  
  fi->feature_type = SEGMENT;
  fi->segment_type = type;
  init_adaptor(fi);
}

void  gt_feature_index_fo_filter_mutations_only(GtFeatureIndexFo *fi){

  fi->feature_type = MUTATION;
  init_adaptor(fi);
}

void  gt_feature_index_fo_filter_translocations_only(GtFeatureIndexFo *fi){
  
  fi->feature_type = TRANSLOCATION;
  init_adaptor(fi);
}

void  gt_feature_index_fo_filter_generic_only(GtFeatureIndexFo *fi){
  fi->feature_type = GENERIC;
  init_adaptor(fi);
}

void  gt_feature_index_fo_reset_filter_type(GtFeatureIndexFo *fi){

  gt_base_query_info_delete(fi->bqi);
}

void  gt_feature_index_fo_unset_all_filters(GtFeatureIndexFo *fi){

  fi->feature_type = 0;
  if(fi->bqi != NULL){
    gt_base_query_info_delete(fi->bqi);
  }
  gt_hashmap_reset(fi->where_clause_filter_int);
  gt_hashmap_reset(fi->where_clause_filter_double);
  gt_hashmap_reset(fi->where_clause_filter_str);
  gt_array_delete(fi->additional_experiments);
  fi->segment_th = 99999.0;
  if(fi->track_id != NULL){
    gt_str_delete(fi->track_id);
    fi->track_id = NULL;
  }
  fi->sorted = false;
  fi->score = 99999.0;
  if(fi->seqid != NULL){
    gt_str_delete(fi->seqid);
    fi->seqid = NULL;
  }
  if(fi->range != NULL){
    fi->range = NULL;
  }
  fi->greater_than_score = false;
}

void gt_feature_index_fo_set_segments_th(GtFeatureIndexFo *fi,
                                               double th){
  fi->segment_th = th;
}

void gt_feature_index_fo_unset_segments_th(GtFeatureIndexFo *fi) {
  fi->segment_th = 99999.0;
}

void gt_feature_index_fo_set_track_id(GtFeatureIndexFo *fi,
                                      const char *track_id){
  if(fi->track_id != NULL){
    gt_str_delete(fi->track_id);
  }
  fi->track_id = gt_str_new_cstr(track_id);
}

void gt_feature_index_fo_unset_track_id(GtFeatureIndexFo *fi){
  if(fi->track_id != NULL){
    gt_str_delete(fi->track_id);
    fi->track_id = NULL;
  }
}

void gt_feature_index_fo_set_segments_sorted(GtFeatureIndexFo *fi,
                                             bool sorted){
  fi->sorted = sorted;
}

void gt_feature_index_fo_set_score(GtFeatureIndexFo *fi,
                                   double score,
                                   bool grater_than){
  fi->score = score;
  fi->greater_than_score = grater_than;
}

void gt_feature_index_fo_unset_score(GtFeatureIndexFo *fi){

  fi->score = 99999.0;
  fi->greater_than_score = false;
}

void gt_feature_index_fo_add_where_clause_int_filter(GtFeatureIndexFo *fi,
                                                     char *column,
                                                     int filter[],
                                                     int length){
														 
  GtArray *filterArr = int_array_to_gt_array(filter, length);
  gt_hashmap_add(fi->where_clause_filter_int, column, filterArr);
  
}

void gt_feature_index_fo_add_where_clause_str_filter(GtFeatureIndexFo *fi,
                                                     char *column,
                                                     char* filter[],
                                                     int length){
														 
  GtArray *filterArr = char_array_to_gt_array(filter, length);
  gt_hashmap_add(fi->where_clause_filter_str, column, filterArr);
}

void gt_feature_index_fo_add_where_clause_double_filter(GtFeatureIndexFo *fi,
                                                        char *column,
                                                        double filter[],
                                                        int length){
														 
  GtArray *filterArr = double_array_to_gt_array(filter, length);
  gt_hashmap_add(fi->where_clause_filter_double, column, filterArr);
}

void gt_feature_index_fo_add_segment_status_filter(GtFeatureIndexFo *fi,
										           int filter[],
												   int length){

  gt_feature_index_fo_add_where_clause_int_filter(fi,
                                                  "segment.status",
                                                  filter,
                                                  length);
}

void gt_feature_index_fo_add_generic_filter(GtFeatureIndexFo *fi,
												char* filter[],
												int length){

  gt_feature_index_fo_add_where_clause_str_filter(fi,
                                                  "feature.feature_type",
                                                  filter,
                                                  length);
}

void gt_feature_index_fo_add_project_filter(GtFeatureIndexFo *fi,
                                            int filter[],
                                            int length){

  gt_feature_index_fo_add_where_clause_int_filter(fi,
                                                  "study_in_project.project_id",
                                                  filter,
                                                  length);
}
void gt_feature_index_fo_add_tissue_filter(GtFeatureIndexFo *fi,
                                           int filter[],
                                           int length){

  gt_feature_index_fo_add_where_clause_int_filter(fi,
                                                  "organ.organ_id",
                                                  filter,
                                                  length);
}

void gt_feature_index_fo_add_somatic_filter(GtFeatureIndexFo *fi,
                                            char* filter[],
                                            int length){
  gt_feature_index_fo_add_where_clause_str_filter(fi,
                                                  "mutation.somatic",
                                                  filter,
                                                  length);
}

void gt_feature_index_fo_add_confidence_filter(GtFeatureIndexFo *fi,
                                               char* filter[],
                                               int length){
  gt_feature_index_fo_add_where_clause_str_filter(fi,
                                                  "mutation.confidence",
                                                  filter,
                                                  length);
}

void gt_feature_index_fo_add_snptool_filter(GtFeatureIndexFo *fi,
                                            char* filter[],
                                            int length){
  gt_feature_index_fo_add_where_clause_str_filter(fi,
                                                  "mutation.snp_tool",
                                                  filter,
                                                  length);
}

void gt_feature_index_fo_reset_where_clause_int_filter(GtFeatureIndexFo *fi){

  gt_hashmap_reset(fi->where_clause_filter_int);
}
void gt_feature_index_fo_reset_where_clause_str_filter(GtFeatureIndexFo *fi){

  gt_hashmap_reset(fi->where_clause_filter_str);
}
void gt_feature_index_fo_reset_where_clause_double_filter(GtFeatureIndexFo *fi){

  gt_hashmap_reset(fi->where_clause_filter_double);
}

void gt_feature_index_fo_set_additional_experiment_filter(GtFeatureIndexFo *fi,
                                                          int filter[],
                                                          int length){
  GtArray *filterArr = int_array_to_gt_array(filter, length);
  fi->additional_experiments = filterArr;
}
                                                          
void gt_feature_index_fo_unset_additional_experiment_filter(
                                                        GtFeatureIndexFo *fi){
  gt_array_delete(fi->additional_experiments);
}

void gt_feature_index_fo_set_location(GtFeatureIndexFo *fi, 
                                       char *seqid,
                                       GtRange *range){
  fi->seqid = gt_str_new_cstr(seqid);
  fi->range = range;
}

void init_adaptor(GtFeatureIndexFo *fi) {

  GtBaseQueryInfo *bqi;

  GtStr *feature_id,
        *seq_id,
        *start,
        *end,
        *study_id,
        *study_name;

  GtArray *columns = NULL;
  GtStr *table = NULL;
  GtStr *ptable = gt_str_new();
  GtStr *tmp_table;
  
  if(fi->feature_type == SEGMENT){
            
    feature_id = gt_str_new_cstr("DISTINCT(segment.segment_id)");
    seq_id = gt_str_new_cstr("segment.chromosome");
    start = gt_str_new_cstr("segment.start");
    end = gt_str_new_cstr("segment.end");
    study_id = gt_str_new_cstr("segment.study_id");
    study_name = gt_str_new_cstr("study.study_name");
   
    columns = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(columns, feature_id);
    gt_array_add(columns, seq_id);
    gt_array_add(columns, start);
    gt_array_add(columns, end);
    gt_array_add(columns, study_id);
    gt_array_add(columns, study_name);
  
    table = gt_str_new_cstr("segment");
    gt_str_append_str(ptable, table);
    gt_str_append_cstr(ptable, ".");
  }
  if(fi->feature_type == MUTATION){
          
    feature_id = gt_str_new_cstr("DISTINCT(mutation.mutation_id)");
    seq_id = gt_str_new_cstr("mutation.chromosome");
    start = gt_str_new_cstr("mutation.start");
    end = gt_str_new_cstr("mutation.end");
    study_id = gt_str_new_cstr("mutation.study_id");
    study_name = gt_str_new_cstr("study.study_name");
   
    columns = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(columns, feature_id);
    gt_array_add(columns, seq_id);
    gt_array_add(columns, start);
    gt_array_add(columns, end);
    gt_array_add(columns, study_id);
    gt_array_add(columns, study_name);
  
    
    table = gt_str_new_cstr("mutation");
    gt_str_append_str(ptable, table);
    gt_str_append_cstr(ptable, ".");
  }
  if(fi->feature_type == TRANSLOCATION){
    
    GtStr* ref_id;
    
    feature_id = gt_str_new_cstr("DISTINCT(translocation.translocation_id)");
    seq_id = gt_str_new_cstr("translocation.chromosome");
    start = gt_str_new_cstr("translocation.start");
    end = gt_str_new_cstr("translocation.end");
    ref_id = gt_str_new_cstr("translocation.translocation_ref_id");
    study_id = gt_str_new_cstr("translocation.study_id");
    study_name = gt_str_new_cstr("study.study_name");
   
    columns = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(columns, feature_id);
    gt_array_add(columns, seq_id);
    gt_array_add(columns, start);
    gt_array_add(columns, end);
    gt_array_add(columns, ref_id);
    gt_array_add(columns, study_id);
    gt_array_add(columns, study_name);
  
    table = gt_str_new_cstr("translocation");
    gt_str_append_str(ptable, table);
    gt_str_append_cstr(ptable, ".");
  }
  if(fi->feature_type == GENERIC){
    
    GtStr* feature_type;
    
    feature_id = gt_str_new_cstr("DISTINCT(feature.feature_id)");
    seq_id = gt_str_new_cstr("feature.chromosome");
    start = gt_str_new_cstr("feature.start");
    end = gt_str_new_cstr("feature.end");
    feature_type = gt_str_new_cstr("feature.feature_type");
    study_id = gt_str_new_cstr("feature.study_id");
    study_name = gt_str_new_cstr("study.study_name");
   
    columns = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(columns, feature_id);
    gt_array_add(columns, seq_id);
    gt_array_add(columns, start);
    gt_array_add(columns, end);
    gt_array_add(columns, feature_type);
    gt_array_add(columns, study_id);
    gt_array_add(columns, study_name);
  
    table = gt_str_new_cstr("feature");
    gt_str_append_str(ptable, table);
    gt_str_append_cstr(ptable, ".");
  }
  
    GtArray *left_joins = gt_array_new(sizeof (GtArray*));
    
    /* study join */
    GtStr *lftable_study, *lfcond_study;

    lftable_study = gt_str_new_cstr("study");
    lfcond_study = gt_str_new_cstr("study_id = study.study_id");
    tmp_table = gt_str_new();
    gt_str_append_str(tmp_table, ptable);
    gt_str_append_str(tmp_table, lfcond_study);
  
    GtArray *lf_study;
  
    lf_study = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(lf_study, lftable_study);
    gt_array_add(lf_study, tmp_table);
  
    gt_str_delete(lfcond_study);
  
    gt_array_add(left_joins, lf_study);
  
    /* project join */
    
    GtStr *lftable_project, *lfcond_project;

    lftable_project = gt_str_new_cstr("study_in_project");
    lfcond_project = gt_str_new_cstr("study.study_id = study_in_project.study_id");
  
    GtArray *lf_project;
  
    lf_project = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(lf_project, lftable_project);
    gt_array_add(lf_project, lfcond_project);
  
    gt_array_add(left_joins, lf_project);
   
    /* tissue join */
   
    GtStr *lftable_tissue, *lfcond_tissue;

    lftable_tissue = gt_str_new_cstr("tissue_sample");
    lfcond_tissue = gt_str_new_cstr("tissue_sample.study_id = study.study_id");
  
    GtArray *lf_tissue;
  
    lf_tissue = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(lf_tissue, lftable_tissue);
    gt_array_add(lf_tissue, lfcond_tissue);
  
    gt_array_add(left_joins, lf_tissue);
   
    /* organ join */
   
    GtStr *lftable_organ, *lfcond_organ;

    lftable_organ = gt_str_new_cstr("organ");
    lfcond_organ = gt_str_new_cstr("organ.organ_id = tissue_sample.tissue_sample_organ_id");
  
    GtArray *lf_organ;
  
    lf_organ = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(lf_organ, lftable_organ);
    gt_array_add(lf_organ, lfcond_organ);
  
    gt_array_add(left_joins, lf_organ);
   
    bqi = gt_base_query_info_new(columns, table, left_joins);
  
    fi->bqi = bqi; 
    
    gt_str_delete(ptable);
}

GtStr* get_maximal_overlap_SQL_where_clause(unsigned long start,
                                            unsigned long end){
		
		GtStr *qry = gt_str_new();
		
		gt_str_append_cstr(qry, " AND ((start <= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND end >= ");
		gt_str_append_ulong(qry, end);
		gt_str_append_cstr(qry, ")");
		
		gt_str_append_cstr(qry, " OR ");
		
		gt_str_append_cstr(qry, "(start >= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND end <= " );
		gt_str_append_ulong(qry, end);
		gt_str_append_cstr(qry, ")");
		
		gt_str_append_cstr(qry, " OR ");
		
		gt_str_append_cstr(qry, "(start >= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND start <= ");
		gt_str_append_ulong(qry, end);
		gt_str_append_cstr(qry, ")");
		
		gt_str_append_cstr(qry, " OR ");
		
		gt_str_append_cstr(qry, "(end >= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND end <= ");
		gt_str_append_ulong(qry, end);
		gt_str_append_cstr(qry, "))");
	    
		return qry;
}

GtStr* get_threshold_SQL_clause(double th){
		
		GtStr *qry = gt_str_new();
		
		if(th > 0){
			gt_str_append_cstr(qry, " AND mean > '");
			gt_str_append_double(qry, th, 6);
			gt_str_append_cstr(qry, "'");
		} else {
			gt_str_append_cstr(qry, " AND mean < '");
			gt_str_append_double(qry, th, 6);
			gt_str_append_cstr(qry, "'");
		}
		
		return qry;
	}

GtStr* get_filter_SQL_where_clause_str(GtStr *column, GtArray *filter) {
  return get_filter_SQL_where_clause(column, filter, STR);
}

GtStr* get_filter_SQL_where_clause_int(GtStr *column, GtArray *filter) {
  return get_filter_SQL_where_clause(column, filter, INT);
}

GtStr* get_filter_SQL_where_clause_double(GtStr *column, GtArray *filter) {
  return get_filter_SQL_where_clause(column, filter, DOUBLE);
}

GtStr* get_filter_SQL_where_clause(GtStr *column,
                                   GtArray *filter,
                                   FilterType type) {
  GtStr *qry = gt_str_new();
  int i;
  if(filter != NULL && gt_array_size(filter) > 0){
    //gt_str_append_cstr(qry, " AND ( ");
    gt_str_append_cstr(qry, " ( ");
    for(i = 0; i < gt_array_size(filter); i++){
      if(i == 0){
        gt_str_append_str(qry, column);
        gt_str_append_cstr(qry, " = '");
        if(type == STR) {
          gt_str_append_cstr(qry, *(char**) gt_array_get(filter, i));
        }
        if(type == INT) {
          gt_str_append_int(qry, *((int*) gt_array_get(filter, i)));
        }
        if(type == DOUBLE) {
          gt_str_append_double(qry, *((double*) gt_array_get(filter, i)), 5);
        }
        gt_str_append_cstr(qry, "'");
      } else {
        gt_str_append_cstr(qry, " OR ");
        gt_str_append_str(qry, column);
        gt_str_append_cstr(qry, " = '");
        if(type == STR) {
          gt_str_append_cstr(qry, *(char**) gt_array_get(filter, i));
        }
        if(type == INT) {
          gt_str_append_int(qry, *((int*) gt_array_get(filter, i)));
        }
        if(type == DOUBLE) {
          gt_str_append_double(qry, *((double*) gt_array_get(filter, i)), 5);
        }
        gt_str_append_cstr(qry, "' ");
      }
    }
    gt_str_append_cstr(qry, " )");
  }
  return qry;
}

GtStr* get_score_SQL_clause(char* column, double score, bool greater_than_score){
	
  GtStr *qry = gt_str_new();
  if(score != 99999.0){
    GtStr *order = gt_str_new();
    if(greater_than_score){
      gt_str_append_cstr(order, " > ");
    } else {
      gt_str_append_cstr(order, " < ");
    }
  
    gt_str_append_cstr(qry, " AND ");
    gt_str_append_cstr(qry, column);
    gt_str_append_str(qry, order);
    gt_str_append_cstr(qry, "'");
    gt_str_append_double(qry, score, 5);
    gt_str_append_cstr(qry, "'");
    
    gt_str_delete(order);
  }
  return qry;
}

int fetch_features(GtFeatureIndexFo *fi,
                   GtArray *results,
                   GtRDBStmt *stmt,
                   const char* track_id,
                   GtError *err)
{
  int had_err = 0;
  GtGenomeNode *newgn;
  GtFeatureNode *newfn;
  int strand = GT_STRAND_BOTH;
  GtHashmap *track_nodes = NULL;
  GtGenomeNode* value;

  if(fi->feature_type == SEGMENT){
    track_nodes = gt_hashmap_new(GT_HASH_STRING, (GtFree) free, NULL);
  } 
  
  while (!had_err && gt_rdb_stmt_exec(stmt, err) == 0) {
	
	unsigned long id = 0;
	GtStr *seq_id = gt_str_new();
    unsigned long start = 0;
    unsigned long end = 0;
    int ref_id = 0;
    GtStr *feature_type = gt_str_new();
    GtStr *study_name = gt_str_new();
    int study_id = 0;
    
    gt_rdb_stmt_get_ulong(stmt, 0, &id, err);
    gt_rdb_stmt_get_string(stmt, 1, seq_id, err);
    gt_rdb_stmt_get_ulong(stmt, 2, &start, err);
    gt_rdb_stmt_get_ulong(stmt, 3, &end, err);
    
    if(fi->feature_type == TRANSLOCATION){
      gt_rdb_stmt_get_int(stmt, 4, &ref_id, err);
	  gt_rdb_stmt_get_int(stmt, 5, &study_id, err);
      gt_rdb_stmt_get_string(stmt, 6, study_name, err);
    } else if (fi->feature_type == GENERIC) {
	  gt_rdb_stmt_get_string(stmt, 4, feature_type, err);
	  gt_rdb_stmt_get_int(stmt, 5, &study_id, err);
      gt_rdb_stmt_get_string(stmt, 6, study_name, err);
	} else {
      gt_rdb_stmt_get_int(stmt, 4, &study_id, err);
      gt_rdb_stmt_get_string(stmt, 5, study_name, err);
    }
    
    char* s_id = malloc(7);
    sprintf(s_id, "%i", study_id);
    
    char *sId;
    
    GtStr *feature_id = gt_str_new();
    gt_str_append_ulong(feature_id, id);
    
    if(fi->feature_type == GENERIC){
      newgn = gt_feature_node_new(seq_id, track_id, start, end, strand);
	  newfn = gt_feature_node_cast(newgn);
	  
	  gt_feature_node_set_attribute(newfn, STUDY_ID, s_id);
	  gt_feature_node_set_attribute(newfn, ID, gt_str_get(feature_id));
	  gt_feature_node_set_attribute(newfn, TYPE, gt_str_get(feature_type));
      gt_feature_node_set_attribute(newfn, NAME, gt_str_get(study_name));
      gt_feature_node_set_attribute(newfn, FEATURE_TYPE, "generic");
	  
	  gt_array_add(results, newgn);
      
    }
    
    if(fi->feature_type == TRANSLOCATION){
      newgn = gt_feature_node_new(seq_id, track_id, start, end, strand);
	  newfn = gt_feature_node_cast(newgn);
	  
	  GtStr* str_ref_id = gt_str_new();
	  gt_str_append_int(str_ref_id, ref_id);
	  
	  gt_feature_node_set_attribute(newfn, STUDY_ID, s_id);
	  gt_feature_node_set_attribute(newfn, ID, gt_str_get(feature_id));
      gt_feature_node_set_attribute(newfn, NAME, gt_str_get(study_name));
      gt_feature_node_set_attribute(newfn, TRANSLOC_REF_ID, gt_str_get(str_ref_id));
      gt_feature_node_set_attribute(newfn, FEATURE_TYPE, "translocation");
	  
	  gt_array_add(results, newgn);
	  gt_str_delete(str_ref_id);
      
    }
    
    if(fi->feature_type == MUTATION){
      newgn = gt_feature_node_new(seq_id, track_id, start, end, strand);
	  newfn = gt_feature_node_cast(newgn);
	  
	  gt_feature_node_set_attribute(newfn, STUDY_ID, s_id);
	  gt_feature_node_set_attribute(newfn, ID, gt_str_get(feature_id));
      gt_feature_node_set_attribute(newfn, NAME, gt_str_get(study_name));
      gt_feature_node_set_attribute(newfn, FEATURE_TYPE, "mutation");
	  
	  gt_array_add(results, newgn);
      
    }
    
    if(fi->feature_type == SEGMENT){
      GtStr *type = gt_str_new();
    
      if(!fi->sorted)
      {
	    newgn = gt_feature_node_new(seq_id, track_id, start, end, strand);
	    newfn = gt_feature_node_cast(newgn);
	  
	    gt_feature_node_set_attribute(newfn, STUDY_ID, s_id);
	    gt_feature_node_set_attribute(newfn, ID, gt_str_get(feature_id));
        gt_feature_node_set_attribute(newfn, NAME, gt_str_get(study_name));
        gt_feature_node_set_attribute(newfn, FEATURE_TYPE, "segment");
	  
	    gt_array_add(results, newgn);
		
	  } else {
    
       value = gt_hashmap_get(track_nodes, gt_str_get(study_name));
    
        if(value == NULL)
        {
	      newgn = gt_feature_node_new(seq_id, track_id, fi->range->start, fi->range->end, strand);
	      newfn = gt_feature_node_cast(newgn);
	  
	      gt_feature_node_set_attribute(newfn, STUDY_ID, s_id);
	      gt_feature_node_set_attribute(newfn, ID, gt_str_get(study_name));
	      gt_feature_node_set_attribute(newfn, FEATURE_TYPE, "segment_root");
	      value = newgn;
	  
	      sId = gt_cstr_dup(gt_str_get(study_name));
	  
	      gt_hashmap_add(track_nodes, sId, newgn);
	  
	      gt_array_add(results, newgn);
	    }
    
      gt_str_append_cstr(type, track_id);
      gt_str_append_cstr(type, "_segments");
    
      newgn = gt_feature_node_new(seq_id, gt_str_get(type), start, end, strand);
      newfn = gt_feature_node_cast(newgn);
    
      gt_feature_node_set_attribute(newfn, ID, gt_str_get(feature_id));
      gt_feature_node_set_attribute(newfn, NAME, gt_str_get(feature_id));
      gt_feature_node_set_attribute(newfn, FEATURE_TYPE, "segment");
    
      gt_feature_node_set_attribute(newfn, STUDY_ID, s_id);
    
      gt_feature_node_add_child(gt_feature_node_cast(value), newfn);
    
      }
    
      gt_str_delete(type);
        
    }
    
    gt_str_delete(study_name);
    gt_str_delete(seq_id);
    gt_str_delete(feature_id);
    gt_str_delete(feature_type);
    free(s_id);
  }
  if(fi->feature_type == SEGMENT){
    gt_hashmap_delete(track_nodes);
  } 
  return had_err;
}

int gt_feature_index_fo_get_features(GtFeatureIndexFo *fi,
                                     GtArray *results,
                                     GtError *err)
{
  int had_err = 0;
  bool first = true;

  GtArray *base_results = gt_array_new(sizeof (GtFeatureNode*));
  GtArray *experiment_results = gt_array_new(sizeof (GtFeatureNode*));
  
  GtRDBStmt *stmt;
  gt_assert(fi);
  gt_error_check(err);
  
  GtStrArray *filter_clause_int = NULL,
  *filter_clause_double = NULL,
  *filter_clause_str = NULL;
  
  GtStr *overlap_clause = NULL,
        *thr_clause = NULL,
        *score_clause = NULL,
        
        *filter_clause = NULL,
        *experiment_clause = NULL,
        *order_clause = NULL;
  
  filter_clause_int = gt_str_array_new();
  filter_clause_double = gt_str_array_new();
  filter_clause_str = gt_str_array_new();
  filter_clause = gt_str_new();
  
  if(fi->range != NULL) {
    overlap_clause = get_maximal_overlap_SQL_where_clause(fi->range->start,
                                                          fi->range->end);
  }
  if(fi->feature_type == SEGMENT){
	if(fi->segment_type == INTENSITY){
		
      char *type_filter[] = {"cnv_intensity"};
		
	  gt_feature_index_fo_add_where_clause_str_filter(fi,
                                                     "segment.type",
                                                     type_filter,
                                                     1);
      thr_clause = get_threshold_SQL_clause(fi->segment_th);
    }
    if(fi->segment_type == STATUS){
	  
	  char *type_filter[] = {"cnv_status"};
		
	    gt_feature_index_fo_add_where_clause_str_filter(fi,
                                                       "segment.type",
                                                       type_filter,
                                                       1);
	}
  }
  if(fi->feature_type == MUTATION){
     score_clause = get_score_SQL_clause("mutation.quality", fi->score, fi->greater_than_score);
  }
  
  gt_hashmap_foreach(fi->where_clause_filter_int, hash_iterate_int,
                              filter_clause_int, err);
  
  gt_hashmap_foreach(fi->where_clause_filter_double, hash_iterate_double,
                              filter_clause_double, err);
  
  gt_hashmap_foreach(fi->where_clause_filter_str, hash_iterate_str,
                              filter_clause_str, err);
  int i;
  if(gt_str_array_size(filter_clause_int) != 0){
	
	for(i = 0; i < gt_str_array_size(filter_clause_int); i++){
      if(i > 0){
	    gt_str_append_cstr(filter_clause, " AND ");
	  }
	    gt_str_append_cstr(filter_clause, gt_str_array_get(filter_clause_int, i));
    }
  }
        
  if(gt_str_array_size(filter_clause_double) != 0){
	  
	if(strcmp(gt_str_get(filter_clause), "") != 0) {
	  gt_str_append_cstr(filter_clause, " AND ");
    }
    
    for(i = 0; i < gt_str_array_size(filter_clause_double); i++){
      if(i > 0){
	    gt_str_append_cstr(filter_clause, " AND ");
	  }
	    gt_str_append_cstr(filter_clause, gt_str_array_get(filter_clause_double, i));
    }
  }
  
  if(gt_str_array_size(filter_clause_str) != 0){
	if(strcmp(gt_str_get(filter_clause), "") != 0) {
	  gt_str_append_cstr(filter_clause, " AND ");
    }
    
    for(i = 0; i < gt_str_array_size(filter_clause_str); i++){
      if(i > 0){
	    gt_str_append_cstr(filter_clause, " AND ");
	  }
	    gt_str_append_cstr(filter_clause, gt_str_array_get(filter_clause_str, i));
    }
  }
  
  gt_str_array_delete(filter_clause_int);
  gt_str_array_delete(filter_clause_double);
  gt_str_array_delete(filter_clause_str);
  
  GtStr *col = gt_str_new_cstr("study.study_id");
  experiment_clause = get_filter_SQL_where_clause_int(col,
                                                    fi->additional_experiments);
  gt_str_delete(col);
  
  GtStr *base_qry = NULL;
  GtStr *filter_qry = gt_str_new();
  GtStr *experiment_qry = gt_str_new();
  base_qry = gt_base_query_info_get_base_query(fi->bqi);
  gt_str_append_cstr(base_qry, " WHERE ");
  if(fi->seqid != NULL) {
	
	first = false;
    gt_str_append_cstr(base_qry, "chromosome = '");
    gt_str_append_cstr(base_qry, gt_str_get(fi->seqid));
    gt_str_append_cstr(base_qry, "'");
  }
  if(fi->range != NULL) {
	first = false;
    gt_str_append_cstr(base_qry, gt_str_get(overlap_clause));
  }
  if(fi->feature_type == SEGMENT && fi->segment_type == INTENSITY){
	first = false;
    gt_str_append_cstr(base_qry, gt_str_get(thr_clause));
  }
  if(score_clause != NULL && fi->feature_type == MUTATION){
	first = false;
    gt_str_append_cstr(base_qry, gt_str_get(score_clause));
  }
  gt_str_append_str(filter_qry, base_qry);
  
  if(strcmp(gt_str_get(filter_clause), "") != 0 && !first){
	  gt_str_append_cstr(filter_qry, " AND ");
  }
  
  gt_str_append_cstr(filter_qry, gt_str_get(filter_clause));
  
  order_clause = gt_str_new_cstr(" ORDER BY ");
  gt_str_append_str(order_clause, gt_base_query_info_get_table(fi->bqi));
  gt_str_append_cstr(order_clause, ".study_id ASC");
  
  gt_str_append_str(filter_qry, order_clause);
  
  stmt = gt_rdb_prepare(fi->db,
                        gt_str_get(filter_qry),
                        0,
                        err);
  
  gt_mutex_lock(fi->dblock);
  
  if(gt_error_is_set(err)){
	  printf("%s \n", gt_error_get(err));
  }
  
  gt_rdb_stmt_reset(stmt, err);
  
  fetch_features(fi,
                 results,
                 stmt,
                 gt_str_get(fi->track_id),
                 err);

  gt_mutex_unlock(fi->dblock);
  
  gt_array_add_array(results, base_results);
  
  if(fi->additional_experiments){
    if(gt_array_size(fi->additional_experiments) > 0){
	  gt_rdb_stmt_delete(stmt);
	  
      gt_str_append_str(experiment_qry, base_qry);
      
      gt_str_append_cstr(experiment_qry, " AND ");
      
      gt_str_append_cstr(experiment_qry, gt_str_get(experiment_clause));
  
      gt_str_append_str(experiment_qry, order_clause);
  
      stmt = gt_rdb_prepare(fi->db,
                            gt_str_get(experiment_qry),
                            0,
                            err);
  
      gt_mutex_lock(fi->dblock);
      gt_rdb_stmt_reset(stmt, err);
  
      fetch_features(fi,
                     results,
                     stmt,
                     gt_str_get(fi->track_id),
                     err);

      gt_mutex_unlock(fi->dblock);
  
      gt_array_add_array(results, experiment_results);
    }
  }
  
  gt_rdb_stmt_delete(stmt);
  
  if(thr_clause){
    gt_str_delete(thr_clause);
  }
  if(score_clause){
    gt_str_delete(score_clause);
  }
  gt_array_delete(base_results);
  gt_array_delete(experiment_results);
  gt_str_delete(overlap_clause);
  gt_str_delete(order_clause);
  
  gt_str_delete(filter_clause);
  gt_str_delete(experiment_clause);
  gt_str_delete(base_qry);
  gt_str_delete(filter_qry);
  gt_str_delete(experiment_qry);
  
  return had_err;
}

int hash_iterate_gene_nodes(void *key, void *value, void *data, GtError *err){
  
  gt_error_check(err);
  GtArray *gene_mutations = (GtArray*) data;
  
  key = NULL;
  
  GtGenomeNode *gene_gn = (GtGenomeNode*) value;
  
  GtFeatureNode *gene_fn = gt_feature_node_cast(gene_gn);
  
  unsigned long mutations_in_gene;
  
  mutations_in_gene = gt_feature_node_number_of_children(gene_fn);
  
  GtStr* nof_mutations_str = gt_str_new();
  
  gt_str_append_int(nof_mutations_str, mutations_in_gene);
  
  gt_feature_node_set_attribute(gene_fn, NOF_MUTATIONS, gt_str_get(nof_mutations_str));
  
  const char *name = gt_feature_node_get_attribute(gene_fn, NAME);
  
  GtStr *str_name = gt_str_new_cstr(name);
  gt_str_append_cstr(str_name, " (");
  gt_str_append_cstr(str_name, gt_str_get(nof_mutations_str));
  gt_str_append_cstr(str_name, ")");
  
  gt_feature_node_set_attribute(gene_fn, NAME, gt_str_get(str_name));
  
  gt_str_delete(nof_mutations_str);
  
  gt_str_delete(str_name);
  
  gt_array_add(gene_mutations, gene_gn);
  
  return 0;
}

char* make_pos_str(GtFeatureNode *fn){

  GtStr *str = gt_str_new();
  char *pos = NULL;
  
  gt_str_append_str(str, gt_genome_node_get_seqid((GtGenomeNode*) fn));
  gt_str_append_cstr(str, "-");
  gt_str_append_ulong(str, gt_genome_node_get_start((GtGenomeNode*) fn));

  pos = gt_cstr_dup(gt_str_get(str));
  
  gt_str_delete(str);

  return pos;

}

int sort_segments(const void *a, const void *b) {
  
  int ret = 0;
  
  GtGenomeNode *gn_a = *(GtGenomeNode**) a;
  GtFeatureNode *fn_a = gt_feature_node_cast(gn_a);
  GtGenomeNode *gn_b = *(GtGenomeNode**) b;
  GtFeatureNode *fn_b = gt_feature_node_cast(gn_b);
  
  unsigned long *gn_a_cumulative_length = (unsigned long*) gt_genome_node_get_user_data(gn_a,
                                    gt_feature_node_get_attribute(fn_a, STUDY_ID));
  unsigned long *gn_b_cumulative_length = (unsigned long*) gt_genome_node_get_user_data(gn_b,
                                    gt_feature_node_get_attribute(fn_b, STUDY_ID));
  
  if(*gn_a_cumulative_length == *gn_b_cumulative_length){
    ret = 0;
  } else {
    if(*gn_a_cumulative_length > *gn_b_cumulative_length){
      ret = 1;
    } else {
	  ret = -1;
    }
  }
  return ret;
}

void set_layout_block_sort(GtLayout *l){

  gt_layout_set_block_ordering_func(l,sort_blocks, NULL);

}

int sort_blocks(const GtBlock *b1, const GtBlock *b2, void *data){
  
  int ret = 0;
  
  data = NULL;
  
  GtGenomeNode *gn_a = (GtGenomeNode*) gt_block_get_top_level_feature((GtBlock*) b1);
  GtGenomeNode *gn_b = (GtGenomeNode*) gt_block_get_top_level_feature((GtBlock*) b2);
  
  GtFeatureNode *fn_a = gt_feature_node_cast(gn_a);
  GtFeatureNode *fn_b = gt_feature_node_cast(gn_b);
  
  if(gt_feature_node_get_attribute(fn_a, "feature_type") != NULL &&
  gt_feature_node_get_attribute(fn_b, "feature_type")!= NULL){
  
    if(strcmp(gt_feature_node_get_attribute(fn_a, "feature_type"),"segment_root") == 0 &&
    strcmp(gt_feature_node_get_attribute(fn_b, "feature_type"),"segment_root") == 0) {
  
      unsigned long *gn_a_cumulative_length = (unsigned long*) gt_genome_node_get_user_data(gn_a,
                                        gt_feature_node_get_attribute(fn_a, "study_id"));
      unsigned long *gn_b_cumulative_length = (unsigned long*) gt_genome_node_get_user_data(gn_b,
                                        gt_feature_node_get_attribute(fn_b, "study_id"));
  
      if(*gn_a_cumulative_length == *gn_b_cumulative_length){
        ret = 0;
      } else {
        if(*gn_a_cumulative_length > *gn_b_cumulative_length){
          ret = -1;
        } else {
	      ret = 1;
        }
      }
    }
  }
  return ret;

}

int gt_feature_index_fo_sort_segments_for_coverage(GtArray *segments) {
  
  int i;
  GtGenomeNode *gn = NULL;
  GtGenomeNode *root_gn = NULL;
  GtFeatureNode *root_fn = NULL;
  GtFeatureNode *fn = NULL;
  unsigned long segment_length = 0;
  unsigned long *all_segments_length = NULL;
  int start, end;
  GtRange root_gn_range;
  GtRange gn_range;
  GtFeatureNodeIterator *fni = NULL;
  
  for(i = 0; i < gt_array_size(segments); i++){
	
	all_segments_length = gt_malloc(sizeof (unsigned long*));
          
    *all_segments_length = 0;
	
    root_gn = *(GtGenomeNode**) gt_array_get(segments, i);
    
    root_fn = gt_feature_node_cast(root_gn);
    
    root_gn_range = gt_genome_node_get_range(root_gn);
    
    fni = gt_feature_node_iterator_new_direct(root_fn);
    
    while((fn = gt_feature_node_iterator_next(fni)) != NULL){
    
      gn = (GtGenomeNode*) fn;
      
      gn_range = gt_genome_node_get_range(gn);
      
      if(root_gn_range.start <= gn_range.start &&
         root_gn_range.end >= gn_range.end) {
      
        segment_length = gt_range_length(&gn_range);
      } else {
		if(root_gn_range.start >= gn_range.start){
			
			start = root_gn_range.start;
		} else {
		  	start = gn_range.start;
		}
		if(root_gn_range.end <= gn_range.end){
		  end = root_gn_range.end;
		} else {
		  end = gn_range.end;
		}
		
		segment_length =  end - start;
	  }
      
      *all_segments_length += segment_length;
    
    }
    
    gt_genome_node_add_user_data(root_gn,
									gt_feature_node_get_attribute(root_fn, STUDY_ID),
									all_segments_length, 
									free);
    
  }
  
  //gt_array_sort(segments, sort_segments);
  
  return 0;
}

int gt_feature_index_fo_process_mutations(GtArray *results,
                                      GtArray *mutations,
                                      GtRDB *rdb,
                                      const char *track_id,
                                      char* biotype_filter[],
                                      int b_length,
                                      GtError *err){
  GtFeatureIndex *genefi = NULL;
  GtAnnoDBSchema *adb;
  
  gt_error_check(err);

  if(gt_array_size(mutations) > 0){
         
    adb = gt_anno_db_ensembl_new();  
    
    genefi = gt_anno_db_schema_get_feature_index(adb, rdb, err);
  
    GtGenomeNode *gn = NULL;
    GtFeatureNode *fn = NULL;
    
    GtGenomeNode *gene_gn = NULL;
    GtFeatureNode *gene_fn = NULL;
  
    int version = 0;
  
    version = gt_anno_db_feature_index_get_version(genefi, err);
  
    GtEnsemblGeneAdaptor* ga = gt_ensembl_gene_adaptor_new(version);
  
    GtHashmap *gene_nodes = NULL;
    GtHashmap *mut_count = NULL;

    GtArray *plain_mut = gt_array_new(sizeof (GtFeatureNode*));

    gene_nodes = gt_hashmap_new(GT_HASH_STRING, (GtFree) free, NULL);
    mut_count = gt_hashmap_new(GT_HASH_STRING, (GtFree) free, (GtFree) free);
  
    int i,j;
    
    GtArray *gene_results;
  
    /* Test for all mutations if they lie in a gene. */
    for(i = 0; i < gt_array_size(mutations); i++){
    
      gene_results = gt_array_new(sizeof (GtFeatureNode*));
    
      gn = *(GtGenomeNode**) gt_array_get(mutations, i);
      
      fn = gt_feature_node_cast(gn);
    
      GtRange rng = gt_genome_node_get_range(gn);
    
      gt_ensembl_fetch_genes_for_range(ga,
                                       genefi,
                                       gene_results,
                                       gt_str_get(gt_genome_node_get_seqid(gn)),
                                       &rng,
                                       biotype_filter,
                                       b_length,
                                       err);
      
      /* if one gene is found test if it was already found */
      if(gt_array_size(gene_results) > 0){
		
		for(j = 0; j < gt_array_size(gene_results); j++){  
		
          gene_gn = *(GtGenomeNode**) gt_array_get(gene_results, j);
        
          gene_fn = gt_feature_node_cast(gene_gn);
        
          GtGenomeNode *stored_gene_gn = NULL;
        
          stored_gene_gn = gt_hashmap_get(gene_nodes, gt_feature_node_get_attribute(gene_fn, ID));
        
          /* if the gene was already found the mutation is added as a child */
          /* to the previously found gene */
          if(stored_gene_gn){
        
             GtFeatureNode *stored_gene_fn = gt_feature_node_cast(stored_gene_gn);
        
             //printf("%s\n", gt_feature_node_get_attribute(stored_gene_fn, NAME));
        
		     GtStr *child_type = gt_str_new_cstr(track_id);
             gt_str_append_cstr(child_type, "_mutations");
        
             gt_feature_node_set_type(fn, gt_str_get(child_type));
        
             gt_str_delete(child_type);
        
             gt_feature_node_add_child(stored_gene_fn, fn);
           
            gt_genome_node_delete(gene_gn);
           
             //what are we going to do if a gene contains one mutation multiple times?
        
          /* Otherwise the mutation is added to the found gene and the gene */
          /* is stored */
          } else {
        
            GtStr *child_type = gt_str_new_cstr(track_id);
            gt_str_append_cstr(child_type, "_mutations");
        
            gt_feature_node_set_type(gene_fn, track_id);
        
            gt_feature_node_set_type(fn, gt_str_get(child_type));
        
            gt_str_delete(child_type);
        
            gt_feature_node_add_child(gene_fn, fn);
          
            gt_feature_node_set_attribute(gene_fn, FEATURE_TYPE, "mutation_root");
            
            char *str = NULL;
            str = gt_cstr_dup(gt_feature_node_get_attribute(gene_fn, ID));
          
            gt_hashmap_add(gene_nodes, str, gene_gn);
        
          }
        }
      }
      if(gt_array_size(gene_results) == 0){
        
        char *pos = make_pos_str(fn);
        
        int *stored_mut_count = gt_hashmap_get(mut_count, pos);
        
        if(stored_mut_count){
          if(*stored_mut_count > 0){
          
            *stored_mut_count = *stored_mut_count + 1;
          
            gt_hashmap_add(mut_count, pos, stored_mut_count);
        
            gt_free(pos);
        
            gt_genome_node_delete(gn);
          }
        } else {
          
          int *new_count = NULL;
          
          new_count = gt_malloc(sizeof (int*));
          
          *new_count = 1;
          
          gt_hashmap_add(mut_count, pos, new_count);
        
          gt_array_add(plain_mut, gn);
          
        }
      }
      gt_array_delete(gene_results);
    }
  
    GtArray *gene_mutations = gt_array_new(sizeof (GtFeatureNode*));
    
    gt_hashmap_foreach(gene_nodes, hash_iterate_gene_nodes, gene_mutations, err);
    
    int *stored_mutcount;
    
    for(i = 0; i < gt_array_size(plain_mut); i++){
    
      gn = *(GtGenomeNode**) gt_array_get(plain_mut, i);
    
      fn = gt_feature_node_cast(gn);
       
      char *pos = make_pos_str(fn);
    
      stored_mutcount = (int*) gt_hashmap_get(mut_count, pos);

      free(pos);
      
      GtStr *nof_mutations_str = gt_str_new();
  
      gt_str_append_int(nof_mutations_str, *stored_mutcount);
      
      gt_feature_node_set_attribute(fn, NOF_MUTATIONS, gt_str_get(nof_mutations_str));
      
      gt_str_delete(nof_mutations_str);
      
    }
    
    gt_array_add_array(results, gene_mutations);
    gt_array_add_array(results, plain_mut);
    gt_array_delete(gene_mutations);
    
    gt_array_delete(plain_mut);
    
    gt_hashmap_delete(gene_nodes);
    gt_hashmap_delete(mut_count);
    
    gt_ensembl_gene_adaptor_delete(ga);
    
    gt_feature_index_delete(genefi);
    gt_anno_db_schema_delete(adb);

  }
  return 0;
}

GtStr* make_transloc_name(GtArray *gene_results, GtGenomeNode *transloc_gn){
  
  int j;
  GtGenomeNode *gene_gn = NULL;
  GtFeatureNode *gene_fn = NULL;
  
  GtStr *transloc_name_str = gt_str_new();
  
  /* If yes, name it after the gene names. */
  if(gt_array_size(gene_results) > 0){

    for(j = 0; j < gt_array_size(gene_results); j++){
		
      gene_gn = *(GtGenomeNode**) gt_array_get(gene_results, j);
        
      gene_fn = gt_feature_node_cast(gene_gn);
            
      gt_str_append_cstr(transloc_name_str, gt_feature_node_get_attribute(gene_fn, NAME));
            
      if(j + 1 != gt_array_size(gene_results)) {
            
        gt_str_append_cstr(transloc_name_str, ",");
            
	  }
    }
          
    /* Else name it after its position chr:pos. */
	} else {
          
      GtStr *chr = NULL;
      GtStr* name = gt_str_new();
      
      chr = gt_genome_node_get_seqid(transloc_gn);
      
      unsigned long pos = gt_genome_node_get_start(transloc_gn);
      
      gt_str_append_str(name, chr);
          
      gt_str_append_cstr(name, ":");
          
      gt_str_append_ulong(name, pos);
          
      gt_str_append_str(transloc_name_str, name);
      
      gt_str_delete(name);
      
    }
        
  return transloc_name_str;
}

int gt_feature_index_fo_process_translocations(GtFeatureIndexFo *fi,
                                      GtArray *translocations,
                                      GtRDB *rdb,
                                      char *track_id,
                                      char* biotype_filter[],
                                      int b_length,
                                      GtError *err){
  GtFeatureIndex *genefi = NULL;
  GtAnnoDBSchema *adb = NULL;
  GtEnsemblGeneAdaptor* ga = NULL;
  int version = 0;
  GtArray *transloc_refs = NULL;
  GtArray *gene_results = NULL;
  GtGenomeNode *transloc_gn = NULL;
  GtFeatureNode *transloc_fn = NULL;
  GtGenomeNode *transloc_ref_gn = NULL;
  GtFeatureNode *transloc_ref_fn = NULL;
  GtGenomeNode *gn = NULL;
  GtStr *name = NULL;
   
  int i,j;
  
  gt_error_check(err);
  
  if(gt_array_size(translocations) > 0) {
    
    adb = gt_anno_db_ensembl_new();
    
    genefi = gt_anno_db_schema_get_feature_index(adb, rdb, err);
  
    version = gt_anno_db_feature_index_get_version(genefi, err);
  
    ga = gt_ensembl_gene_adaptor_new(version);
    
    for(i = 0; i < gt_array_size(translocations); i++) {
  
      /* Get reference node for translocation. */
      gt_feature_index_fo_unset_all_filters(fi);
    
      gt_feature_index_fo_filter_translocations_only(fi);
    
      gt_feature_index_fo_set_track_id(fi, track_id);
    
      transloc_gn = *(GtGenomeNode**) gt_array_get(translocations, i);
  
      transloc_fn = gt_feature_node_cast(transloc_gn);
      
      char *ref_id = NULL;
      
      ref_id = (char*) gt_feature_node_get_attribute(transloc_fn, TRANSLOC_REF_ID);
      
      char *filter[] = {ref_id};
    
      gt_feature_index_fo_add_where_clause_str_filter(fi,
                                                      "translocation.translocation_id",
                                                      filter,
                                                      1);
    
      transloc_refs = gt_array_new(sizeof (GtFeatureNode*));
    
      gt_feature_index_fo_get_features(fi,
                                       transloc_refs,
                                       err);
    
      /* Test if translocation nodes lie within one or more genes. */
      
      if(gt_array_size(transloc_refs) == 1){
		
		/* fetch genes for transloc node */
	    gene_results = gt_array_new(sizeof (GtFeatureNode*));
    
        GtRange rng = gt_genome_node_get_range(transloc_gn);
        
        gt_ensembl_fetch_genes_for_range(ga,
                                         genefi,
                                         gene_results,
                                         gt_str_get(gt_genome_node_get_seqid(transloc_gn)),
                                         &rng,
                                         biotype_filter,
                                         b_length,
                                         err);
        
        name = make_transloc_name(gene_results, transloc_gn);
                                         
        gt_feature_node_set_attribute(transloc_fn, NAME, gt_str_get(name));
	    
	    for(j = 0; j < gt_array_size(gene_results); j++) {
          gn = *(GtGenomeNode**) gt_array_get(gene_results, j);
          gt_genome_node_delete(gn);
        }
	    
	    gt_str_delete(name);
	    gt_array_delete(gene_results);
	    
	    /* fetch genes for transloc_ref node */
	    gene_results = gt_array_new(sizeof (GtFeatureNode*));
	    
	    transloc_ref_gn = *(GtGenomeNode**) gt_array_get(transloc_refs, 0);
  
        transloc_ref_fn = gt_feature_node_cast(transloc_ref_gn);
	    
	    rng = gt_genome_node_get_range(transloc_ref_gn);
        
        gt_ensembl_fetch_genes_for_range(ga,
                                         genefi,
                                         gene_results,
                                         gt_str_get(gt_genome_node_get_seqid(transloc_ref_gn)),
                                         &rng,
                                         biotype_filter,
                                         b_length,
                                         err);
                                         
        name = make_transloc_name(gene_results, transloc_ref_gn);
                                         
        gt_feature_node_set_attribute(transloc_ref_fn, NAME, gt_str_get(name));
	    
	    for(j = 0; j < gt_array_size(gene_results); j++) {
          gn = *(GtGenomeNode**) gt_array_get(gene_results, j);
          gt_genome_node_delete(gn);
        }
	    
	    gt_str_delete(name);
	    gt_array_delete(gene_results);
        
	    /* Merge transloc_ref name into transloc name and set transloc_ref_id. */
	    
	    GtStr *transloc_name = NULL;
	    GtStr *transloc_ref_name = NULL;
	    
	    transloc_name = gt_str_new_cstr(gt_feature_node_get_attribute(transloc_fn, NAME));
	    
	    transloc_ref_name = gt_str_new_cstr(gt_feature_node_get_attribute(transloc_ref_fn, NAME));
	    
	    gt_str_append_cstr(transloc_name, "<->");
	    gt_str_append_str(transloc_name, transloc_ref_name);
	    
	    gt_str_delete(transloc_ref_name);
	    
	    gt_feature_node_set_attribute(transloc_fn, NAME, gt_str_get(transloc_name));
	    
	    gt_str_delete(transloc_name);
	    gt_genome_node_delete(transloc_ref_gn);
	    
	  } else {
		  
		  printf("Warning: missing reference location node!");
	  }
	  
	  gt_array_delete(transloc_refs);
	  
    }
    
    gt_ensembl_gene_adaptor_delete(ga);
    gt_feature_index_delete(genefi);
    gt_anno_db_schema_delete(adb);
  }

  return 0;
}

void gt_feature_index_fo_delete(GtFeatureIndex *gfi)
{
  GtFeatureIndexFo *fi;
  if (!gfi) return;
  fi = feature_index_fo_cast(gfi);
  if (fi->db)
    gt_rdb_delete(fi->db);
  if(fi->bqi){
    gt_base_query_info_delete(fi->bqi);
  }
  if(fi->where_clause_filter_int){
    gt_hashmap_delete(fi->where_clause_filter_int);
  }
  if(fi->where_clause_filter_double){
    gt_hashmap_delete(fi->where_clause_filter_double);
  }
  if(fi->where_clause_filter_str){
    gt_hashmap_delete(fi->where_clause_filter_str);
  }
  if(fi->additional_experiments){
    gt_array_delete(fi->additional_experiments);
  }
  if(fi->seqid){
    gt_str_delete(fi->seqid);
  }
  if(fi->track_id){
    gt_str_delete(fi->track_id);
  }
  
  gt_mutex_delete(fi->dblock);
}

const GtFeatureIndexClass* feature_index_fo_class(void)
{
  static const GtFeatureIndexClass *fic = NULL;
  if (!fic) {
    fic = gt_feature_index_class_new(sizeof (GtFeatureIndexFo),
                                gt_feature_index_fo_add_region_node,
                                gt_feature_index_fo_add_feature_node,
                                gt_feature_index_fo_remove_node,
                                gt_feature_index_fo_get_features_for_seqid,
                                gt_feature_index_fo_get_features_for_range,
                                gt_feature_index_fo_get_first_seqid,
                                gt_feature_index_fo_save,
                                gt_feature_index_fo_get_seqids,
                                gt_feature_index_fo_get_range_for_seqid,
                                gt_feature_index_fo_get_orig_range_for_seqid,
                                gt_feature_index_fo_has_seqid,
                                gt_feature_index_fo_delete);
  }
  return fic;
}

GtFeatureIndex* anno_db_fo_build(GtAnnoDBSchema *schema, GtRDB *db,
                                      GtError *err)
{
  int had_err = 0;
  GtFeatureIndex *fi = NULL;
  GtFeatureIndexFo *fis;
  GtAnnoDBFo *ade;
  gt_assert(schema && db);
  gt_error_check(err);

  ade = anno_db_fo_cast(schema);
  had_err = gt_rdb_accept(db, ade->visitor, err);

  if (!had_err) {
    fi = gt_feature_index_create(feature_index_fo_class());
    fis = feature_index_fo_cast(fi);
    fis->where_clause_filter_int = gt_hashmap_new(GT_HASH_STRING,
                                                 NULL,
                                                 (GtFree) gt_array_delete);
    fis->where_clause_filter_double = gt_hashmap_new(GT_HASH_STRING,
                                                     NULL,
                                                     (GtFree) gt_array_delete);
    fis->where_clause_filter_str = gt_hashmap_new(GT_HASH_STRING,
                                                 NULL,
                                                 (GtFree) gt_array_delete);
    fis->segment_th = 99999.0;
    fis->score = 99999.0;
    fis->seqid = NULL;
    fis->range = NULL;
    fis->db = gt_rdb_ref(db);
  }
  return fi;
}

const GtAnnoDBSchemaClass* gt_anno_db_fo_class()
{
  static const GtAnnoDBSchemaClass *adbsc = NULL;
  if (!adbsc) {
    adbsc = gt_anno_db_schema_class_new(sizeof (GtAnnoDBFo),
                                        anno_db_fo_free,
                                        anno_db_fo_build);
  }
  return adbsc;
}

static const GtRDBVisitorClass* fo_setup_visitor_class()
{
  static const GtRDBVisitorClass *svc = NULL;
  if (!svc) {
    svc = gt_rdb_visitor_class_new(sizeof (GtAnnoDBFo),
                                   NULL,
                                   NULL,
                                   NULL);
  }
  return svc;
}

static GtRDBVisitor* fo_setup_visitor_new(GtAnnoDBFo *adb)
{
  GtRDBVisitor *v = gt_rdb_visitor_create(fo_setup_visitor_class());
  FoSetupVisitor *sv = fo_setup_visitor_cast(v);
  gt_assert(adb);
  sv->annodb = adb;
  return v;
}

GtAnnoDBSchema* gt_anno_db_fo_new(void)
{
  GtAnnoDBSchema *s = gt_anno_db_schema_create(gt_anno_db_fo_class());
  GtAnnoDBFo *ade = anno_db_fo_cast(s);
  ade->visitor = fo_setup_visitor_new(ade);
  return s;
}

int gt_anno_db_fo_unit_test(GtError *err)
{
  int had_err = 0;
  /*
  GtFeatureIndex *fi = NULL;
  GtFeatureIndexFo *fifo = NULL;
  GtAnnoDBSchema *adb;
  
  GtRDB *rdb_fo;
  */
  GtRDB *rdb_e;
  /*
  gt_error_check(err);

  rdb_fo = gt_rdb_mysql_new("localhost",
                           3306,
                           "testoracle",
                           "fouser",
                           "fish4me",
                           err);
  */
  rdb_e = gt_rdb_mysql_new("localhost",
                           3306,
                           "ensembl66",
                           "fouser",
                           "fish4me",
                           err);
  /*
  adb = gt_anno_db_fo_new();
  
  fi = gt_anno_db_schema_get_feature_index(adb, rdb_fo, err);
  gt_ensure(had_err, fi);
  
  fifo = feature_index_fo_cast(fi);
  */
  int i;
  GtGenomeNode *gn = NULL;
  GtFeatureNode *fn = NULL;
  //GtArray *results = NULL;
  GtArray *test_results = NULL;
  GtArray *sorted_results = NULL;
  char *biotype_filter[] = {""};
  
  /* fetch segments */
  /* Replace with test suite test */
  /*
  int project_filter[] = {1};
  
  results = gt_array_new(sizeof (GtFeatureNode*));
  
  GtRange qry_range = {88986342 , 90713681 };
  
  gt_feature_index_fo_filter_segment_only(fifo);
  gt_feature_index_fo_set_segments_lower_th(fifo, -0.9);
  gt_feature_index_fo_set_track_id(fifo, "test1");
  gt_feature_index_fo_set_segments_sorted(fifo, false);
  gt_feature_index_fo_add_where_clause_int_filter(fifo,
                                                  "study_in_project.project_id",
                                                  project_filter,
                                                  1);
  
  gt_feature_index_fo_get_features(fifo,
                                   results,
                                   "10",
                                   &qry_range,
                                   err);
  
  gt_ensure(had_err,  gt_array_size(results) == 11);
  
  
  for(i = 0; i < gt_array_size(results); i++){
  
    gn = *(GtGenomeNode**) gt_array_get(results,i);
  
    fn = gt_feature_node_cast(gn);
  
    //gt_ensure(had_err,  strcmp(gt_feature_node_get_attribute(fn, "ID"), "KLLN") == 0);
  
    printf(" %s %s %lu %lu %s %s %s %s \n",
    gt_str_get(gt_genome_node_get_seqid(gn)),
    gt_feature_node_get_type(fn),
    gt_genome_node_get_start(gn),
    gt_genome_node_get_end(gn),
    gt_feature_node_get_attribute(fn, STUDY_ID),
    gt_feature_node_get_attribute(fn, ID),
    gt_feature_node_get_attribute(fn, NAME),
    gt_feature_node_get_attribute(fn, ROOT));
  
    gt_genome_node_delete(gn);
  
  }
  
  gt_array_delete(results);
  */
  printf("---------------------------------\n");
  
  /* fetch mutations */
  /* Replace with test suite test */
  /*
  10       88354279        .       9       G       A       (Rep G A)
  10       89098879        .       13      G       T       (Rep G T)
  10       90857478        rs11202977      32      A       T       (Rep A T)
  10       91317453        .       47      A       G       (Rep A G)
  10       91579126        .       17      A       T       (Rep A T)
  */
  
  //results = gt_array_new(sizeof (GtFeatureNode*));
  
  //GtRange qry_range2 = {88000000 , 92000000 };
  
  //gt_feature_index_fo_reset_filter_type(fifo);
  
  //gt_feature_index_fo_filter_mutations_only(fifo);
  /*
  gt_feature_index_fo_get_features(fifo,
                                   results,
                                   "10",
                                   &qry_range2,
                                   err);
  
  
  
  gt_ensure(had_err,  gt_array_size(results) == 5);
  
  for(i = 0; i < gt_array_size(results); i++){
  
    gn = *(GtGenomeNode**) gt_array_get(results,i);
  
    fn = gt_feature_node_cast(gn);
  
    //gt_ensure(had_err,  strcmp(gt_feature_node_get_attribute(fn, "ID"), "KLLN") == 0);
  
    printf(" %s %s %lu %lu %s %s %s %s \n",
    gt_str_get(gt_genome_node_get_seqid(gn)),
    gt_feature_node_get_type(fn),
    gt_genome_node_get_start(gn),
    gt_genome_node_get_end(gn),
    gt_feature_node_get_attribute(fn, STUDY_ID),
    gt_feature_node_get_attribute(fn, ID),
    gt_feature_node_get_attribute(fn, NAME),
    gt_feature_node_get_attribute(fn, ROOT));
  
    gt_genome_node_delete(gn);
  }
  
  gt_array_delete(results);
  */
  printf("---------------------------------\n");
  
  /* test mutation specific feature tree building */
  /* Add ensures... */
  
  test_results = gt_array_new(sizeof (GtFeatureNode*));
  sorted_results = gt_array_new(sizeof (GtFeatureNode*));
  GtStr *str;
  
  str = gt_str_new_cstr("10");
  
  gn = gt_feature_node_new(str, "mutation", 89625776, 89625776, GT_STRAND_BOTH);
  fn = gt_feature_node_cast(gn);
	  
  gt_feature_node_set_attribute(fn, STUDY_ID, "1");
  gt_feature_node_set_attribute(fn, ID, "test1");
  gt_feature_node_set_attribute(fn, NAME, "5");
  gt_feature_node_set_attribute(fn, FEATURE_TYPE, "mutation");
  
  gt_array_add(test_results, gn);
  
  gt_str_delete(str);
  
  str = gt_str_new_cstr("10");
  
  gn = gt_feature_node_new(str, "mutation", 89635776, 89635776, GT_STRAND_BOTH);
  fn = gt_feature_node_cast(gn);
  
  gt_feature_node_set_attribute(fn, STUDY_ID, "1");
  gt_feature_node_set_attribute(fn, ID, "test1");
  gt_feature_node_set_attribute(fn, NAME, "6");
  gt_feature_node_set_attribute(fn, FEATURE_TYPE, "mutation");
  
  gt_array_add(test_results, gn);
  
  gt_str_delete(str);
  
  str = gt_str_new_cstr("10");
  
  gn = gt_feature_node_new(str, "mutation", 89632776, 89632776, GT_STRAND_BOTH);
  fn = gt_feature_node_cast(gn);
  
  gt_feature_node_set_attribute(fn, STUDY_ID, "1");
  gt_feature_node_set_attribute(fn, ID, "test1");
  gt_feature_node_set_attribute(fn, NAME, "7");
  gt_feature_node_set_attribute(fn, FEATURE_TYPE, "mutation");
  
  gt_array_add(test_results, gn);
  
  gt_str_delete(str);
  
  str = gt_str_new_cstr("10");
  
  gn = gt_feature_node_new(str, "mutation", 99632776, 99632776, GT_STRAND_BOTH);
  fn = gt_feature_node_cast(gn);
  
  gt_feature_node_set_attribute(fn, STUDY_ID, "1");
  gt_feature_node_set_attribute(fn, ID, "test1");
  gt_feature_node_set_attribute(fn, NAME, "8");
  gt_feature_node_set_attribute(fn, FEATURE_TYPE, "mutation");
  
  gt_array_add(test_results, gn);
  
  gt_str_delete(str);
  
  str = gt_str_new_cstr("10");
  
  gn = gt_feature_node_new(str, "mutation", 89903054, 89903054, GT_STRAND_BOTH);
  fn = gt_feature_node_cast(gn);
  
  gt_feature_node_set_attribute(fn, STUDY_ID, "1");
  gt_feature_node_set_attribute(fn, ID, "test1");
  gt_feature_node_set_attribute(fn, NAME, "9");
  gt_feature_node_set_attribute(fn, FEATURE_TYPE, "mutation");
  
  gt_array_add(test_results, gn);
  
  gt_str_delete(str);
  
  str = gt_str_new_cstr("10");
  
  gn = gt_feature_node_new(str, "mutation", 89959903, 89959903, GT_STRAND_BOTH);
  fn = gt_feature_node_cast(gn);
  
  gt_feature_node_set_attribute(fn, STUDY_ID, "1");
  gt_feature_node_set_attribute(fn, ID, "test1");
  gt_feature_node_set_attribute(fn, NAME, "10");
  gt_feature_node_set_attribute(fn, FEATURE_TYPE, "mutation");
  
  gt_array_add(test_results, gn);
  
  gt_str_delete(str);
  
   str = gt_str_new_cstr("10");
  
  gn = gt_feature_node_new(str, "mutation", 89959903, 89959903, GT_STRAND_BOTH);
  fn = gt_feature_node_cast(gn);
  
  gt_feature_node_set_attribute(fn, STUDY_ID, "1");
  gt_feature_node_set_attribute(fn, ID, "test1");
  gt_feature_node_set_attribute(fn, NAME, "11");
  gt_feature_node_set_attribute(fn, FEATURE_TYPE, "mutation");
  
  gt_array_add(test_results, gn);
  
  gt_str_delete(str);
  
  gt_feature_index_fo_process_mutations(sorted_results,
                                      test_results,
                                      rdb_e,
                                      "track",
                                      biotype_filter,
                                      0,
                                      err);
  
  GtFeatureNodeIterator *fni;
  
  GtFeatureNode *root_fn = NULL;
  
  for(i = 0; i < gt_array_size(sorted_results); i++){
  
    gn = *(GtGenomeNode**) gt_array_get(sorted_results,i);
  
    root_fn = gt_feature_node_cast(gn);
  
    fni = gt_feature_node_iterator_new(root_fn);
    
    while((fn = gt_feature_node_iterator_next(fni)) != NULL){
	
	  printf(" %s %s (%s) %lu %lu %s %s %s %s \n",
      gt_str_get(gt_genome_node_get_seqid(gn)),
      gt_feature_node_get_type(fn),
      gt_feature_node_get_attribute(fn, NOF_MUTATIONS),
      gt_genome_node_get_start((GtGenomeNode*) fn),
      gt_genome_node_get_end((GtGenomeNode*) fn),
      gt_feature_node_get_attribute(fn, STUDY_ID),
      gt_feature_node_get_attribute(fn, ID),
      gt_feature_node_get_attribute(fn, NAME),
      gt_feature_node_get_attribute(fn, FEATURE_TYPE));
      
	}
  
    gt_genome_node_delete((GtGenomeNode*) root_fn);
    
    gt_feature_node_iterator_delete(fni);
    
  }
  
  gt_array_delete(sorted_results);
  gt_array_delete(test_results);
  
  //gt_feature_index_delete(fi);
  //gt_rdb_delete(rdb_fo);
  gt_rdb_delete(rdb_e);
  //gt_anno_db_schema_delete(adb);
  
  return had_err;
}
