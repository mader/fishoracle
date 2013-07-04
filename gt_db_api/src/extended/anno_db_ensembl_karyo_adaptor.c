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
#include "core/ma.h"
#include "extended/anno_db_ensembl_api.h"
#include "extended/anno_db_ensembl_karyo_adaptor_api.h"
#include "extended/sql_base_query_info_api.h"
#include "extended/genome_node.h"
#include "extended/feature_node.h"
#include "core/ensure.h"

struct GtEnsemblKaryoAdaptor {
  GtBaseQueryInfo *bqi;
};

GtEnsemblKaryoAdaptor* gt_ensembl_karyo_adaptor_new(int ensembl_version)
{
   GtBaseQueryInfo *bqi = NULL;
  
  if(ensembl_version >= 54)
  {
    GtStr *karyo_id,
          *seq_region_name,
          *start,
          *end,
          *band;
        
    karyo_id = gt_str_new_cstr("karyotype.karyotype_id");
    seq_region_name = gt_str_new_cstr("seq_region.name");
    start = gt_str_new_cstr("karyotype.seq_region_start");
    end = gt_str_new_cstr("karyotype.seq_region_end");
    band = gt_str_new_cstr("karyotype.band");

    GtArray *columns = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(columns, karyo_id);
    gt_array_add(columns, seq_region_name);
    gt_array_add(columns, start);
    gt_array_add(columns, end);
    gt_array_add(columns, band);
  
    GtStr *table;
    table = gt_str_new_cstr("karyotype");
  
    GtStr *lftable_seq_region, *lfcond_seq_region;
  
    lftable_seq_region = gt_str_new_cstr("seq_region");
    lfcond_seq_region = gt_str_new_cstr("karyotype.seq_region_id = seq_region.seq_region_id");
  
    GtArray *lf_seq_region;
  
    lf_seq_region= gt_array_new(sizeof (GtStr*));
  
    gt_array_add(lf_seq_region, lftable_seq_region);
    gt_array_add(lf_seq_region, lfcond_seq_region);
  
    GtArray *left_joins = gt_array_new(sizeof (GtArray*));
  
    gt_array_add(left_joins, lf_seq_region);
    
    bqi = gt_base_query_info_new(columns, table, left_joins);
      
  }
  
  GtEnsemblKaryoAdaptor *ka = gt_malloc(sizeof *ka);
  ka->bqi = bqi;
    
  return ka;
}

int gt_ensembl_fetch_range_for_karyoband(GtEnsemblKaryoAdaptor *ka,
                                         GtFeatureIndex *gfi,
                                         GtRange *range,
                                         const char *chr,
                                         const char *band,
                                         GtError *err)
{
  int had_err = 0;
  gt_error_check(err);
  GtRDBStmt *stmt;
  gt_assert(gfi);
  gt_error_check(err);
  
  GtRDB* db = gt_feature_index_ensembl_get_db(gfi);
  
  GtStr *qry = gt_str_new();
  
  GtStr* left_join = gt_base_query_info_get_left_join(ka->bqi);
  
  gt_str_append_cstr(qry, "SELECT "
                          "min(seq_region_start), "
                          "max(seq_region_end) "
                          "FROM karyotype");          
  gt_str_append_str(qry, left_join);
  gt_str_append_cstr(qry, " WHERE name = ");
  gt_str_append_cstr(qry, chr);
  gt_str_append_cstr(qry, " AND band REGEXP '");
  gt_str_append_cstr(qry, band);
  gt_str_append_cstr(qry, "'");
  
  stmt = gt_rdb_prepare(db,
                        gt_str_get(qry),
                        0,
                        err);
  
  gt_rdb_stmt_reset(stmt, err);
  
  if (!had_err && gt_rdb_stmt_exec(stmt, err) == 0) {
	
    unsigned long seq_region_start = 0;
    unsigned long seq_region_end = 0;
    
    gt_rdb_stmt_get_ulong(stmt, 0, &seq_region_start, err);
    gt_rdb_stmt_get_ulong(stmt, 1, &seq_region_end, err);
    
    range->start = seq_region_start;
    range->end = seq_region_end;
     
  }
  
  gt_str_delete(left_join);
  gt_str_delete(qry);
  gt_rdb_stmt_delete(stmt);
  
  return had_err;
}

int gt_ensembl_fetch_karyobands_for_range(GtEnsemblKaryoAdaptor *ka,
                                          GtFeatureIndex *gfi,
                                          GtArray *results,
                                          const char *seqid,
                                          GtRange *qry_range,
                                          GtError *err)
{
  int had_err = 0;
  
  GtRDBStmt *stmt;
  gt_assert(gfi && results);
  gt_error_check(err);
  
  GtRDB* db = gt_feature_index_ensembl_get_db(gfi);
  
  GtStr *qry = gt_base_query_info_get_base_query(ka->bqi);
  
  GtStr* overlap_clause = get_overlap_where_clause(seqid,
                                                   qry_range->start,
                                                   qry_range->end);
  
  gt_str_append_str(qry, overlap_clause);
  gt_str_append_cstr(qry, " ORDER BY karyotype.karyotype_id ASC");
  
  stmt = gt_rdb_prepare(db,
                        gt_str_get(qry),
                        0,
                        err);
  
  gt_rdb_stmt_reset(stmt, err);
  
  GtGenomeNode *newgn;
  GtFeatureNode *newfn;
  int strand = GT_STRAND_BOTH;
  GtStr* karyoband;
  
  while (!had_err && gt_rdb_stmt_exec(stmt, err) == 0) {
	  
	unsigned long karyoband_id;
    GtStr *seq_region_name = gt_str_new();
    unsigned long seq_region_start = 0;
    unsigned long seq_region_end = 0;
    GtStr *band = gt_str_new();
    
    gt_rdb_stmt_get_ulong(stmt, 0, &karyoband_id, err);
    gt_rdb_stmt_get_string(stmt, 1, seq_region_name, err);
    gt_rdb_stmt_get_ulong(stmt, 2, &seq_region_start, err);
    gt_rdb_stmt_get_ulong(stmt, 3, &seq_region_end, err);
    gt_rdb_stmt_get_string(stmt, 4, band, err);
    
    newgn = gt_feature_node_new(seq_region_name,
                               "karyoband", seq_region_start,
                                 seq_region_end, strand);
    newfn = gt_feature_node_cast(newgn);
    
    karyoband = gt_str_clone(seq_region_name);
    
    gt_str_append_str(karyoband, band);
    
    gt_feature_node_set_attribute(newfn, "ID", gt_str_get(karyoband));
    
    gt_str_delete(karyoband);
    gt_str_delete(seq_region_name);
    gt_str_delete(band);
    
    gt_assert(newgn && newfn);
    
    gt_array_add(results, newgn);
  }
  
  gt_str_delete(overlap_clause);
  gt_str_delete(qry);
  gt_rdb_stmt_delete(stmt);
  
  return had_err;
}

void gt_ensembl_karyo_adaptor_delete(GtEnsemblKaryoAdaptor *ka)
{
  gt_base_query_info_delete(ka->bqi);
  gt_free(ka);
}

/* This test is specific for the ensembl version 66 */
int gt_ensembl_karyo_adaptor_unit_test(GtError *err)
{
  int had_err = 0;
  GtFeatureIndex *fi = NULL;
  GtAnnoDBSchema *adb;
  
  GtRDB *rdb;
  
  gt_error_check(err);

  rdb = gt_rdb_mysql_new("localhost",
                           3306,
                           "ensembl66",
                           "fouser",
                           "fish4me",
                           err);
                           
  adb = gt_anno_db_ensembl_new();

  fi = gt_anno_db_schema_get_feature_index(adb, rdb, err);
  gt_ensure(had_err, fi);
  
  GtEnsemblKaryoAdaptor* ka = gt_ensembl_karyo_adaptor_new(66);
  
  GtGenomeNode *gn = NULL;
  GtFeatureNode *fn = NULL;
  
  GtRange range = { 0, 0 };
  
  /* Test fetch range for karyoband */
  
  gt_ensembl_fetch_range_for_karyoband(ka, fi, &range, "10", "q23.31", err);
  
  gt_ensure(had_err, range.start == 89500001);
  gt_ensure(had_err, range.end == 92900000);
  
  
  /* Test fetch karyobands for range */
  
  GtArray *results = gt_array_new(sizeof (GtFeatureNode*));
  
  GtRange qry_range = { 90000000, 92000000 };
  
  gt_ensembl_fetch_karyobands_for_range(ka, fi, results, "10", &qry_range, err);
  
  GtStr *seqid = NULL;
  
  int i;
  
  for(i = 0; i < gt_array_size(results); i++)
  {
	  
	gn = *(GtGenomeNode**) gt_array_get(results,i);
	 
	fn = gt_feature_node_cast(gn);
	
	gt_ensure(had_err,  strcmp(gt_feature_node_get_attribute(fn, "ID"), "10q23.31") == 0);
	
	seqid = gt_genome_node_get_seqid(gn);
	
	gt_ensure(had_err,  strcmp(gt_str_get(seqid), "10") == 0);
	
	gt_ensure(had_err,  gt_genome_node_get_start(gn) == 89500001);
	
	gt_ensure(had_err,  gt_genome_node_get_end(gn) == 92900000);
	 
    gt_genome_node_delete(gn);
  }
 
  gt_array_delete(results);
  
  gt_ensembl_karyo_adaptor_delete(ka);
  gt_feature_index_delete(fi);
  gt_anno_db_schema_delete(adb);
  gt_rdb_delete((GtRDB*) rdb);
  return had_err;
}
