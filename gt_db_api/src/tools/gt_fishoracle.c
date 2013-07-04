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

#ifndef WITHOUT_CAIRO
#include <string.h>
#include "extended/feature_index_api.h"
#include "extended/feature_stream_api.h"
#include "core/fileutils_api.h"
#include "core/ma.h"
#include "core/password_entry.h"
#include "core/str_api.h"
#include "core/unused_api.h"
#include "extended/anno_db_fo_api.h"
#include "extended/anno_db_schema_api.h"
#include "extended/gff3_visitor.h"
#include "extended/feature_node.h"
#include "extended/rdb_api.h"
#ifdef HAVE_MYSQL
#include "extended/rdb_mysql_api.h"
#endif
#include "tools/gt_featureindex.h"
#include "core/parseutils_api.h"
#include "core/cstr_api.h"

#define GT_FETCH_TYPE_SEGMENT_STRING "segments"
#define GT_FETCH_TYPE_MUTATION_STRING  "mutations"
#define GT_FETCH_TYPE_TRANSLOCATION_STRING  "translocations"
#define GT_FETCH_TYPE_GENERIC_STRING  "generic"

typedef struct {
  GtRange qry_rng;
  GtStr *seqid;
  GtStr *fetch_type,
        *segment_type,
        *trackid;
  GtStrArray *project_filter,
             *tissue_filter,
             *somatic,
             *confidence,
             *snp_tool,
             *exp_filter,
             *generic_feature_type,
             *status_filter;
  double th, quality;
  bool sorted, process, sort_cov;
  GtStr *host,
        *user,
        *pass,
        *database;
  int port;
  bool verbose;
} GtFishoracleArguments;

static void* gt_fishoracle_arguments_new(void)
{
  GtFishoracleArguments *arguments = gt_calloc(1, sizeof *arguments);
  arguments->qry_rng.start = 0;
  arguments->qry_rng.end = 0;
  arguments->seqid = gt_str_new();
  arguments->fetch_type = gt_str_new();
  arguments->segment_type = gt_str_new();
  arguments->trackid = gt_str_new();
  arguments->project_filter = gt_str_array_new();
  arguments->tissue_filter = gt_str_array_new();
  arguments->somatic = gt_str_array_new();
  arguments->confidence = gt_str_array_new();
  arguments->snp_tool = gt_str_array_new();
  arguments->exp_filter = gt_str_array_new();
  arguments->generic_feature_type = gt_str_array_new();
  arguments->status_filter = gt_str_array_new();
  arguments->host = gt_str_new();
  arguments->user = gt_str_new();
  arguments->pass = gt_str_new();
  arguments->database = gt_str_new();
  return arguments;
}

static void gt_fishoracle_arguments_delete(void *tool_arguments)
{
  GtFishoracleArguments *arguments = tool_arguments;
  if (!arguments) return;
  gt_str_delete(arguments->seqid);
  gt_str_delete(arguments->fetch_type);
  gt_str_delete(arguments->segment_type);
  gt_str_delete(arguments->trackid);
  gt_str_array_delete(arguments->project_filter);
  gt_str_array_delete(arguments->tissue_filter);
  gt_str_array_delete(arguments->somatic);
  gt_str_array_delete(arguments->confidence);
  gt_str_array_delete(arguments->snp_tool);
  gt_str_array_delete(arguments->exp_filter);
  gt_str_array_delete(arguments->generic_feature_type);
  gt_str_array_delete(arguments->status_filter);
  gt_str_delete(arguments->host);
  gt_str_delete(arguments->user);
  gt_str_delete(arguments->pass);
  gt_str_delete(arguments->database);
  gt_free(arguments);
}

static GtOptionParser* gt_fishoracle_option_parser_new(void *tool_arguments)
{
  GtFishoracleArguments *arguments = tool_arguments;
  GtOptionParser *op;
  GtOption *option;
  gt_assert(arguments);

  static const char *fetch_types[] = {
    "segments", /* the default */
    "mutations",
    "translocations",
    "generic",
    NULL
  };

  static const char *segment_types[] = {
    "dnacopy", /* the default */
    "penncnv",
    NULL
  };

  /* init */
  op = gt_option_parser_new("[option ...] indexfilename",
                            "Retrieve annotations from a fish oracle database "
                            "GFF3 output.");

  option = gt_option_new_range("range",
                               "range constraint for index query",
                               &arguments->qry_rng,
                               NULL);
  gt_option_parser_add_option(op, option);

  option = gt_option_new_string("seqid", "sequence region",
                                arguments->seqid, NULL);
  gt_option_parser_add_option(op, option);
  
  
  option = gt_option_new_choice("fetch",
                                "specify segments|mutations|translocations|generic",
                                arguments->fetch_type,
                                fetch_types[0],
                                fetch_types);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_choice("segment-type",
                                "specify dnacopy|penncnv",
                                arguments->segment_type,
                                segment_types[0],
                                segment_types);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_string_array("generic-type",
                                      "type of generic feature",
                                       arguments->generic_feature_type);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_double("lower_th",
                                "lower segment mean threshold",
                                (double*) &arguments->th,
                                99999.0);
  gt_option_parser_add_option(op, option);

  option = gt_option_new_string_array("stati",
                                      "status filter",
                                       arguments->status_filter);
  gt_option_parser_add_option(op, option);

  option = gt_option_new_string("trackid",
                                "set track label",
                                arguments->trackid,
                                "tracktitle");
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_bool("sorted",
                              "sort segments",
                               (bool*) &arguments->sorted,
                               false);
  gt_option_parser_add_option(op, option);
  
   option = gt_option_new_bool("sort-coverage",
                              "sort segments for coverage",
                               (bool*) &arguments->sort_cov,
                               false);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_bool("process",
                              "post process elements",
                               (bool*) &arguments->process,
                               false);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_string_array("projects",
                                      "project filter",
                                       arguments->project_filter);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_string_array("tissues",
                                      "tissue filter",
                                       arguments->tissue_filter);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_double("quality",
                                "snp quality filter",
                                (double*) &arguments->quality,
                                99999.0);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_string_array("somatic",
                                      "snp somatic filter",
                                       arguments->somatic);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_string_array("confidence",
                                      "snp confidence filter",
                                       arguments->confidence);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_string_array("snp-tool",
                                      "snp tool filter",
                                       arguments->snp_tool);
  gt_option_parser_add_option(op, option);
  
  option = gt_option_new_string_array("add-experiments",
                                      "additional experiments",
                                       arguments->exp_filter);
  gt_option_parser_add_option(op, option);
  
#ifdef HAVE_MYSQL
  /* -host */
  option = gt_option_new_string("host", "hostname for database connection",
                                arguments->host, NULL);
  gt_option_parser_add_option(op, option);

  /* -user */
  option = gt_option_new_string("user", "username for database connection",
                                arguments->user, NULL);
  gt_option_parser_add_option(op, option);

  /* -password */
  option = gt_option_new_string("password", "password for database connection",
                                arguments->pass, "");
  gt_option_parser_add_option(op, option);

  /* -database */
  option = gt_option_new_string("database",
                                "database name for database connection",
                                arguments->database, NULL);
  gt_option_parser_add_option(op, option);

  /* -port */
  option = gt_option_new_int_max("port", "port for database connection",
                                 &arguments->port, 3333, 65534);
  gt_option_parser_add_option(op, option);
#endif

  option = gt_option_new_verbose(&arguments->verbose);
  gt_option_parser_add_option(op, option);

  return op;
}

static int gt_fishoracle_arguments_check(GT_UNUSED int rest_argc,
                                           GT_UNUSED void *tool_arguments,
                                           GT_UNUSED GtError *err)
{
  GT_UNUSED GtFishoracleArguments *arguments = tool_arguments;
  int had_err = 0;
  gt_error_check(err);
  gt_assert(arguments);

  return had_err;
}

int get_int_array(GtStrArray *arr, int *filter) {
  int i;
  int had_err = 0;
  int* id = NULL; 
	
  for(i = 0; i < gt_str_array_size(arr); i++){
	  id = gt_malloc(sizeof (int*));
	  had_err = gt_parse_int(id, gt_str_array_get(arr, i));
	  filter[i] = *id;
	  gt_free(id);
  }
  
  return had_err;
}

int get_double_array(GtStrArray *arr, double *filter) {
  int i;
  int had_err = 0;
  double* val = NULL; 
	
  for(i = 0; i < gt_str_array_size(arr); i++){
	  val = gt_malloc(sizeof (double*));
	  had_err = gt_parse_double(val, gt_str_array_get(arr, i));
	  filter[i] = *val;
	  gt_free(val);
  }
  
  return had_err;
}

int get_str_array(GtStrArray *arr, char* filter[]) {
  int i;
  int had_err = 0;
	
  for(i = 0; i < gt_str_array_size(arr); i++){
	  filter[i] = (char*) gt_str_array_get(arr, i);
  }
  
  return had_err;
}

static int gt_fishoracle_runner(GT_UNUSED int argc,
                                  GT_UNUSED const char **argv,
                                  GT_UNUSED int parsed_args,
                                  void *tool_arguments,
                                  GtError *err)
{
  GtFishoracleArguments *arguments = tool_arguments;
  GtFeatureIndex *fi = NULL;
  GtFeatureIndexFo *fifo = NULL;
  GtArray *results = NULL;
  GtRDB *rdb = NULL;
  GtAnnoDBSchema *adbs = NULL;
  GtNodeVisitor *gff3visitor = NULL;
  unsigned long i = 0;
  int had_err = 0;

  gt_error_check(err);
  gt_assert(arguments);

#ifdef HAVE_MYSQL
  if (!had_err) {
	if(strcmp(gt_str_get(arguments->pass), "") == 0){
      gt_str_delete(arguments->pass);
      arguments->pass = gt_get_password("password: ", err);
    }
    rdb = gt_rdb_mysql_new(gt_str_get(arguments->host),
                           arguments->port,
                           gt_str_get(arguments->database),
                           gt_str_get(arguments->user),
                           gt_str_get(arguments->pass),
                           err);
    if (!rdb)
      had_err = -1;
  }
#endif
  if (!had_err)
    adbs = gt_anno_db_fo_new();

  if (!had_err && !adbs)
    had_err = -1;

  if (!had_err) {
    fi = gt_anno_db_schema_get_feature_index(adbs, rdb, err);
    had_err = fi ? 0 : -1;
    fifo = feature_index_fo_cast(fi);
  }

  if (!had_err) {
    if(strcmp(gt_str_get(arguments->seqid),"") != 0 && 
       (arguments->qry_rng.start != 0 && arguments->qry_rng.start !=0 )){
      gt_feature_index_fo_set_location(fifo,
                                       gt_str_get(arguments->seqid),
                                       &arguments->qry_rng);
    }
  }

  if (!had_err) {
    if(strcmp(gt_str_get(arguments->fetch_type), GT_FETCH_TYPE_SEGMENT_STRING) == 0) {
	  
	  int type = 0;
	  
	  if(strcmp(gt_str_get(arguments->segment_type), "dnacopy") == 0){
	    type = INTENSITY;
	  }
	  
	  if(strcmp(gt_str_get(arguments->segment_type), "penncnv") == 0){
	    type = STATUS;
	  }
	  
      gt_feature_index_fo_filter_segment_only(fifo, type);
    }
    if(strcmp(gt_str_get(arguments->fetch_type), GT_FETCH_TYPE_MUTATION_STRING) == 0) {
      gt_feature_index_fo_filter_mutations_only(fifo);
    }
    if(strcmp(gt_str_get(arguments->fetch_type), GT_FETCH_TYPE_TRANSLOCATION_STRING) == 0) {
      gt_feature_index_fo_filter_translocations_only(fifo);
    }
    if(strcmp(gt_str_get(arguments->fetch_type), GT_FETCH_TYPE_GENERIC_STRING) == 0) {
      gt_feature_index_fo_filter_generic_only(fifo);
    }
  }

  if (!had_err) {
    if(arguments->th != 99999.0){
      gt_feature_index_fo_set_segments_th(fifo, arguments->th);
    }
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->status_filter) > 0){
	  int *filter = gt_malloc((sizeof (int*)) * gt_str_array_size(arguments->status_filter));
	  had_err = get_int_array(arguments->status_filter, filter);
      gt_feature_index_fo_add_segment_status_filter(fifo,
                                                   filter,
                                                   gt_str_array_size(
                                                   arguments->status_filter));
     gt_free(filter);
    }
  }

  if (!had_err) {
   gt_feature_index_fo_set_track_id(fifo, gt_str_get(arguments->trackid));
  }

  if (!had_err) {
    gt_feature_index_fo_set_segments_sorted(fifo, arguments->sorted);
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->project_filter) > 0){
	  int *filter = gt_malloc((sizeof (int*)) * gt_str_array_size(arguments->project_filter));
	  had_err = get_int_array(arguments->project_filter, filter);
      gt_feature_index_fo_add_project_filter(fifo,
                                             filter,
                                             gt_str_array_size(arguments->project_filter));
     gt_free(filter);
    }
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->tissue_filter) > 0){
	  int *filter = gt_malloc((sizeof (int*)) * gt_str_array_size(arguments->tissue_filter));
	  had_err = get_int_array(arguments->tissue_filter, filter);
      gt_feature_index_fo_add_tissue_filter(fifo,
                                           filter,
                                           gt_str_array_size(arguments->tissue_filter));
      gt_free(filter);
    }
  }

  if (!had_err) {
	if(arguments->quality != 99999.0){
	  gt_feature_index_fo_set_score(fifo, arguments->quality, true);
    }
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->somatic) > 0){
	  char **filter = gt_malloc((sizeof (char*)) * gt_str_array_size(arguments->somatic));
	  had_err = get_str_array(arguments->somatic, filter);
      gt_feature_index_fo_add_somatic_filter(fifo,
                                             filter,
                                             gt_str_array_size(arguments->somatic));
      gt_free(filter);
    }
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->confidence) > 0){
	  char **filter = gt_malloc((sizeof (char*)) * gt_str_array_size(arguments->confidence));
	  had_err = get_str_array(arguments->confidence, filter);
      gt_feature_index_fo_add_confidence_filter(fifo,
                                                filter,
                                                gt_str_array_size(arguments->confidence));
      gt_free(filter);
    }
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->snp_tool) > 0){
	  char **filter = gt_malloc((sizeof (char*)) * gt_str_array_size(arguments->snp_tool));
	  had_err = get_str_array(arguments->snp_tool, filter);
      gt_feature_index_fo_add_snptool_filter(fifo,
                                             filter,
                                             gt_str_array_size(arguments->snp_tool));
      gt_free(filter);
    }
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->exp_filter) > 0){
	  int *filter = gt_malloc((sizeof (int*)) * gt_str_array_size(arguments->exp_filter));
	  had_err = get_int_array(arguments->exp_filter, filter);
      gt_feature_index_fo_set_additional_experiment_filter(fifo,
                                                           filter,
                                                           gt_str_array_size(arguments->exp_filter));
       gt_free(filter);
    }
  }

  if (!had_err) {
	if(gt_str_array_size(arguments->generic_feature_type) > 0){
	  char **filter = gt_malloc((sizeof (char*)) * gt_str_array_size(arguments->generic_feature_type));
	  had_err = get_str_array(arguments->generic_feature_type, filter);
      gt_feature_index_fo_add_generic_filter(fifo,
                                             filter,
                                             gt_str_array_size(arguments->generic_feature_type));
      gt_free(filter);
    }
  }

  if (!had_err) {
    results = gt_array_new(sizeof (GtFeatureNode*));
    had_err = gt_feature_index_fo_get_features(fifo,
                                              results,
                                              err);
  }
  
  if (!had_err) {
	if(arguments->sort_cov){
		gt_feature_index_fo_sort_segments_for_coverage(results);
	}
  }
  
  if (!had_err) {
	if(arguments->process){
    
      if(strcmp(gt_str_get(arguments->fetch_type), GT_FETCH_TYPE_TRANSLOCATION_STRING) == 0) {
		
		GtRDB *rdb_e = gt_rdb_mysql_new("localhost",
                           3306,
                           "ensembl66",
                           "fouser",
                           "fish4me",
                           err);
		
	    char *biotype_filter[] = {""};
        had_err = gt_feature_index_fo_process_translocations(fifo,
                                        results,
                                        rdb_e,
                                        gt_str_get(arguments->trackid),
                                        biotype_filter,
                                        0,
                                        err);
        gt_rdb_delete(rdb_e);
      }
      
      
      if(strcmp(gt_str_get(arguments->fetch_type), GT_FETCH_TYPE_MUTATION_STRING) == 0) {
		
		GtRDB *rdb_e = gt_rdb_mysql_new("localhost",
                           3306,
                           "ensembl66",
                           "fouser",
                           "fish4me",
                           err);
		
		GtArray *proc_results = gt_array_new(sizeof (GtFeatureNode*));
		
	    char *biotype_filter[] = {""};
        had_err = gt_feature_index_fo_process_mutations(proc_results,
                                          results,
                                          rdb_e,
                                          gt_str_get(arguments->trackid),
                                          biotype_filter,
                                          0,
                                          err);
                                          
        gt_array_delete(results);
        
        results = proc_results;
        
        gt_rdb_delete(rdb_e);
      }
      
    }
  }
  
  if (!had_err) {
    gff3visitor = gt_gff3_visitor_new(NULL);
    for (i=0; i<gt_array_size(results); i++) {
      GtGenomeNode *gn = *(GtGenomeNode**) gt_array_get(results, i);
      
      gt_genome_node_accept(gn, gff3visitor, err);
      //gt_genome_node_delete(gn);
    }
  }

  gt_array_delete(results);
  gt_node_visitor_delete(gff3visitor);
  gt_rdb_delete(rdb);
  gt_anno_db_schema_delete(adbs);

  gt_feature_index_delete(fi);
  return had_err;
}

GtTool* gt_fishoracle(void)
{
  return gt_tool_new(gt_fishoracle_arguments_new,
                  gt_fishoracle_arguments_delete,
                  gt_fishoracle_option_parser_new,
                  gt_fishoracle_arguments_check,
                  gt_fishoracle_runner);
}
#endif
