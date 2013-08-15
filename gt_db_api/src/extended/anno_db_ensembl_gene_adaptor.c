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
#include "extended/anno_db_ensembl_gene_adaptor_api.h"
#include "extended/sql_base_query_info_api.h"
#include "extended/genome_node.h"
#include "extended/feature_node.h"
#include "core/ensure.h"

#define ID "ID"
#define NAME "Name"
#define BIOTYPE "biotype"
#define DESCRIPTION "description"
#define ROOT "root"


struct GtEnsemblGeneAdaptor {
  GtBaseQueryInfo *bqi;
};

GtEnsemblGeneAdaptor* gt_ensembl_gene_adaptor_new(int ensembl_version)
{
  GtBaseQueryInfo *bqi;
  
  GtStr *gene_id,
        *biotype,
        *seq_region_name,
        *start,
        *end,
        *strand,
        *xref_label,
        *description,
        *stable_id;
          
  gene_id = gt_str_new_cstr("gene.gene_id");
  biotype = gt_str_new_cstr("gene.biotype");
  seq_region_name = gt_str_new_cstr("seq_region.name");
  start = gt_str_new_cstr("gene.seq_region_start");
  end = gt_str_new_cstr("gene.seq_region_end");
  strand = gt_str_new_cstr("seq_region_strand");
  xref_label = gt_str_new_cstr("xref.display_label");
  description = gt_str_new_cstr("gene.description");
    
  if(ensembl_version >= 65)
  {
    stable_id = gt_str_new_cstr("gene.stable_id");
  }
  else
  {
    stable_id = gt_str_new_cstr("gene_stable_id.stable_id");
  }
  
  GtArray *columns = gt_array_new(sizeof (GtStr*));
  
  gt_array_add(columns, gene_id);
  gt_array_add(columns, biotype);
  gt_array_add(columns, seq_region_name);
  gt_array_add(columns, start);
  gt_array_add(columns, end);
  gt_array_add(columns, strand);
  gt_array_add(columns, xref_label);
  gt_array_add(columns, description);
  gt_array_add(columns, stable_id);
  
  GtStr *table;
  table = gt_str_new_cstr("gene");
  
  GtArray *left_joins = gt_array_new(sizeof (GtArray*));
  
  if(ensembl_version >= 54 && ensembl_version <= 64)
  {
    /* stable id join */
    GtStr *lftable_stable_id, *lfcond_stable_id;
  
    lftable_stable_id = gt_str_new_cstr("gene_stable_id");
    lfcond_stable_id = gt_str_new_cstr("gene.gene_id = gene_stable_id.gene_id");
  
    GtArray *lf_stable_id;
  
    lf_stable_id = gt_array_new(sizeof (GtStr*));
  
    gt_array_add(lf_stable_id, lftable_stable_id);
    gt_array_add(lf_stable_id, lfcond_stable_id);
  
    gt_array_add(left_joins, lf_stable_id);
    
  }
  
  /* seq_region join */
  GtStr *lftable_seq_region, *lfcond_seq_region;
  
  lftable_seq_region = gt_str_new_cstr("seq_region");
  lfcond_seq_region = gt_str_new_cstr("gene.seq_region_id = seq_region.seq_region_id");
  
  GtArray *lf_seq_region;
  
  lf_seq_region = gt_array_new(sizeof (GtStr*));
  
  gt_array_add(lf_seq_region, lftable_seq_region);
  gt_array_add(lf_seq_region, lfcond_seq_region);
  
  gt_array_add(left_joins, lf_seq_region);
  
  /* xref join */
  GtStr *lftable_xref, *lfcond_xref;
  
  lftable_xref = gt_str_new_cstr("xref");
  lfcond_xref = gt_str_new_cstr("gene.display_xref_id = xref.xref_id");
  
  GtArray *lf_xref;
  
  lf_xref = gt_array_new(sizeof (GtStr*));
  
  gt_array_add(lf_xref, lftable_xref);
  gt_array_add(lf_xref, lfcond_xref);
  
  gt_array_add(left_joins, lf_xref);
    
  bqi = gt_base_query_info_new(columns, table, left_joins);
  
  GtEnsemblGeneAdaptor *ga = gt_malloc(sizeof *ga);
  ga->bqi = bqi;
    
  return ga;
}

int get_nodes_for_stmt(GtArray *results,
                       GtRDBStmt *stmt,
                       GtError *err)
{
  int had_err = 0;
  GtGenomeNode *newgn;
  GtFeatureNode *newfn;
  int strand = GT_STRAND_UNKNOWN;
  
  gt_assert(results && stmt);
  
  while (!had_err && gt_rdb_stmt_exec(stmt, err) == 0) {
	
    unsigned long gene_id;
    GtStr *biotype = gt_str_new();
    GtStr *seq_region_name = gt_str_new();
    unsigned long seq_region_start = 0;
    unsigned long seq_region_end = 0;
    int seq_region_strand = 0;
    GtStr *display_label = gt_str_new();
    GtStr *description = gt_str_new();
    GtStr *stable_id = gt_str_new();
    
    gt_rdb_stmt_get_ulong(stmt, 0, &gene_id, err);
    gt_rdb_stmt_get_string(stmt, 1, biotype, err);
    gt_rdb_stmt_get_string(stmt, 2, seq_region_name, err);
    gt_rdb_stmt_get_ulong(stmt, 3, &seq_region_start, err);
    gt_rdb_stmt_get_ulong(stmt, 4, &seq_region_end, err);
    gt_rdb_stmt_get_int(stmt, 5, &seq_region_strand, err);
    gt_rdb_stmt_get_string(stmt, 6, display_label, err);
    gt_rdb_stmt_get_string(stmt, 7, description, err);
    gt_rdb_stmt_get_string(stmt, 8, stable_id, err);
     
     if(seq_region_strand == 1)
     {
       strand = GT_STRAND_FORWARD;
     }
     if(seq_region_strand == -1)
     {
        strand = GT_STRAND_REVERSE;
     }
     
    newgn = gt_feature_node_new(seq_region_name,
                               "gene", seq_region_start,
                                 seq_region_end, strand);
    newfn = gt_feature_node_cast(newgn);
    
    gt_feature_node_set_attribute(newfn, NAME, gt_str_get(display_label));
    gt_feature_node_set_attribute(newfn, ID, gt_str_get(stable_id));
    gt_feature_node_set_attribute(newfn, BIOTYPE, gt_str_get(biotype));
    if(gt_str_length(description) > 0){
      gt_feature_node_set_attribute(newfn, DESCRIPTION, gt_str_get(description));
    } else {
	  gt_feature_node_set_attribute(newfn, DESCRIPTION, "Not available.");
	}
    gt_feature_node_set_attribute(newfn, ROOT, "NO");
    
    gt_str_delete(seq_region_name);
    gt_str_delete(biotype);
    gt_str_delete(display_label);
    gt_str_delete(description);
    gt_str_delete(stable_id);
    
    gt_array_add(results, newgn);
  }
  
  return had_err;
}

GtStr* get_biotype_SQL_clause(GtArray *biotype_arr)
{
	GtStr *qry = gt_str_new();
	
	int i;
	
	if(biotype_arr!= NULL && gt_array_size(biotype_arr) > 0)
	{
	  gt_str_append_cstr(qry, " AND (");
	  char* filter = NULL;
	  
	  for(i = 0; i < gt_array_size(biotype_arr); i++)
	  {
		  
		  filter = *(char**) gt_array_get(biotype_arr, i);
		  
		  if(i != 0)
		  {
		    gt_str_append_cstr(qry, " OR ");
		  }
		  
		  if(strcmp(filter, "protein_coding") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'protein_coding' ");
		  }
		  if(strcmp(filter, "pseudogene") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'pseudogene' ");
		  }
		  if(strcmp(filter, "processed_transcript") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'processed_transcript' ");
		  }
		  if(strcmp(filter, "polymorphic_pseudogene") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'polymorphic_pseudogene' ");
		  }
		  if(strcmp(filter, "lincRNA") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'lincRNA' ");
		  }
		  if(strcmp(filter, "antisense") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'antisense' ");
		  }
		  if(strcmp(filter, "sense_intronic") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'sense_intronic' ");
		  }
		  if(strcmp(filter, "non_coding") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'non_coding' ");
		  }
		  if(strcmp(filter, "sense_overlapping") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'sense_overlapping' ");
		  }
		  if(strcmp(filter, "3prime_overlapping_ncrna") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = '3prime_overlapping_ncrna' ");
		  }
		  if(strcmp(filter, "ncrna_host") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'ncrna_host'");
		  }
		  if(strcmp(filter, "TR_gene") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'TR_V_gene' OR "
			                          " biotype = 'TR_V_pseudogene' OR "
			                          " biotype = 'TR_D_gene' OR "
			                          " biotype = 'TR_J_gene' OR "
			                          " biotype = 'TR_C_gene' OR "
			                          " biotype = 'TR_J_pseudogene' ");
		  }
		  if(strcmp(filter, "IG_gene") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'IG_C_pseudogene' OR "
			                          " biotype = 'IG_C_gene' OR "
			                          " biotype = 'IG_J_gene' OR "
			                          " biotype = 'IG_V_gene' OR "
			                          " biotype = 'IG_V_pseudogene' OR "
			                          " biotype = 'IG_J_pseudogene' OR "
			                          " biotype = 'IG_D_gene' ");
		  }
		  if(strcmp(filter, "other RNA") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'Mt_tRNA' OR "
			                          " biotype = 'Mt_rRNA' OR "
			                          " biotype = 'miRNA' OR "
			                          " biotype = 'miRNA_pseudogene' OR "
			                          " biotype = 'misc_RNA' OR "
			                          " biotype = 'misc_RNA_pseudogene' OR "
			                          " biotype = 'Mt_tRNA_pseudogene' OR "
			                          " biotype = 'rRNA' OR "
			                          " biotype = 'rRNA_pseudogene' OR "
			                          " biotype = 'scRNA_pseudogene' OR "
			                          " biotype = 'snoRNA' OR "
			                          " biotype = 'snoRNA_pseudogene' OR "
			                          " biotype = 'snRNA' OR "
			                          " biotype = 'snRNA_pseudogene' OR "
			                          " biotype = 'tRNA_pseudogene' ");
		  }
		  if(strcmp(filter, "LRG_gene") == 0)
		  {
			  gt_str_append_cstr(qry, " biotype = 'LRG_gene'");
		  }  
	  }
	  
	  gt_str_append_cstr(qry, ") ");
	  
    }
    return qry;
}

int gt_ensembl_fetch_gene_for_symbol(GtEnsemblGeneAdaptor *ga,
                                     GtFeatureIndex *gfi,
                                     GtGenomeNode **gn,
                                     const char *gene_name,
                                     GtError *err)
{
  int retval;
  GtArray *results = gt_array_new(sizeof (GtGenomeNode*));
  
  GtRDBStmt *stmt;
  gt_assert(gfi);
  
  gt_error_check(err);
  
  GtRDB* db = gt_feature_index_ensembl_get_db(gfi);
  
  GtStr *qry = gt_base_query_info_get_base_query(ga->bqi);
  
  gt_str_append_cstr(qry, " WHERE xref.display_label = '");
  gt_str_append_cstr(qry, gene_name);
  gt_str_append_cstr(qry, "'");
  
  stmt = gt_rdb_prepare(db, gt_str_get(qry), 0, err);
  
  gt_rdb_stmt_reset(stmt, err);
  
  retval = get_nodes_for_stmt(results, stmt, err);
  
  if(gt_array_size(results) > 0)
  {
   *gn = *(GtGenomeNode**) gt_array_get(results, 0);
  }
  else
  {
    gt_error_set(err, "Gene %s not found!", gene_name);
  }
  
  gt_array_delete(results);
  gt_str_delete(qry);
  gt_rdb_stmt_delete(stmt);
  
  return retval;
}

int gt_ensembl_fetch_gene_for_stable_id(GtEnsemblGeneAdaptor *ga,
                                     GtFeatureIndex *gfi,
                                     GtGenomeNode **gn,
                                     const char *stable_id,
                                     GtError *err)
{
  int retval;
  GtArray *results = gt_array_new(sizeof (GtGenomeNode*));
  
  GtRDBStmt *stmt;
  gt_assert(gfi);
  
  gt_error_check(err);
  
  GtRDB* db = gt_feature_index_ensembl_get_db(gfi);
  
  GtStr *qry = gt_base_query_info_get_base_query(ga->bqi);
  
  gt_str_append_cstr(qry, " WHERE stable_id = '");
  gt_str_append_cstr(qry, stable_id);
  gt_str_append_cstr(qry, "'");
  
  stmt = gt_rdb_prepare(db, gt_str_get(qry), 0, err);
  
  gt_rdb_stmt_reset(stmt, err);
  
  retval = get_nodes_for_stmt(results, stmt, err);
  
  if(gt_array_size(results) > 0)
  {
   *gn = *(GtGenomeNode**) gt_array_get(results, 0);
  }
  else 
  {
    gt_error_set(err, "Stable ID %s not found!", stable_id);
  }
  
  gt_array_delete(results);
  gt_str_delete(qry);
  gt_rdb_stmt_delete(stmt);
  
  return retval;
}

GtArray* char_array_to_gt_array(char* arr[], int length)
{
  
    GtArray *gt_arr = gt_array_new(sizeof(char*));
  
    int i;
  
    for(i = 0; i < length; i++)
    {
      gt_array_add(gt_arr, arr[i]);
    }
  
  return gt_arr;
}

int gt_ensembl_fetch_genes_for_range(GtEnsemblGeneAdaptor *ga,
                                      GtFeatureIndex *gfi,
                                      GtArray *results,
                                      const char *seqid,
                                      const GtRange *qry_range,
                                      char *biotype_filter[],
                                      int b_length,
                                      GtError *err)
{
  int retval;
  gt_error_check(err);
  GtRDBStmt *stmt;
  gt_assert(gfi && results);
  gt_error_check(err);
  
  GtRDB* db = gt_feature_index_ensembl_get_db(gfi);
  
  GtArray *b_filter = char_array_to_gt_array(biotype_filter, b_length);
  
  GtStr *overlap_clause;
  overlap_clause = get_overlap_where_clause(seqid,
                                            qry_range->start,
                                            qry_range->end);
  
  GtStr *biotype_clause;
  biotype_clause = get_biotype_SQL_clause(b_filter);   
  
  GtStr *qry = NULL;
  
  qry = gt_base_query_info_get_base_query(ga->bqi);
  
  gt_str_append_str(qry, overlap_clause);
  gt_str_append_str(qry, biotype_clause);
  gt_str_append_cstr(qry, " ORDER BY gene.gene_id ASC");
  
  stmt = gt_rdb_prepare(db,
                        gt_str_get(qry),
                        0,
                        err);
  
  retval = get_nodes_for_stmt(results, stmt, err);
  
  gt_str_delete(overlap_clause);
  gt_str_delete(biotype_clause);
  gt_str_delete(qry);
  gt_array_delete(b_filter);
  
  gt_rdb_stmt_delete(stmt);
  
  return retval;
}

void gt_ensembl_gene_adaptor_delete(GtEnsemblGeneAdaptor *ga)
{
  gt_base_query_info_delete(ga->bqi);
  gt_free(ga);
}

/* This test is specific for the ensembl version 66 */
int gt_ensembl_gene_adaptor_unit_test(GtError *err)
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
  gt_ensure(fi);
  
  GtGenomeNode *gn = NULL;
  GtFeatureNode *fn = NULL;
  
  int version = 0;
  
  version = gt_anno_db_feature_index_get_version(fi, err);
  
  GtEnsemblGeneAdaptor* ga = gt_ensembl_gene_adaptor_new(version);
  
  /* Test fetch gene for gene symbol */
  gt_ensembl_fetch_gene_for_symbol(ga, fi, &gn, "PTEN", err);
  
  gt_ensure(gn);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(gt_feature_node_get_strand(fn) == GT_STRAND_FORWARD);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "PTEN") == 0);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, NAME), "ENSG00000171862") == 0);
  
  gt_genome_node_delete(gn);
  
  /* Test fetch gene for gene stable id */
  
  gt_ensembl_fetch_gene_for_stable_id(ga, fi, &gn, "ENSG00000171862", err);
  
  gt_ensure(gn);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(gt_feature_node_get_strand(fn) == GT_STRAND_FORWARD);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "PTEN") == 0);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, NAME), "ENSG00000171862") == 0);
  
  gt_genome_node_delete(gn);
  
  /* Test with empty biotype filter: everything will be fetched. */
  
  GtArray *results = gt_array_new(sizeof (GtFeatureNode*));
  
  GtRange qry_range = { 89595000, 89635000 };
  
  char *biotype_filter[] = {""};
  
  gt_ensembl_fetch_genes_for_range(ga,
                                   fi,
                                   results,
                                   "10",
                                   &qry_range,
                                   biotype_filter,
                                   0,
                                   err);
   
  gt_ensure(gt_array_size(results) == 4);
  
  gn = *(GtGenomeNode**) gt_array_get(results,0);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "KLLN") == 0);
  
  gt_genome_node_delete(gn);
  
  gn = *(GtGenomeNode**) gt_array_get(results,1);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "CFL1P1") == 0);
  
  gt_genome_node_delete(gn);
  
  gn = *(GtGenomeNode**) gt_array_get(results,2);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "ATAD1") == 0);
  
  gt_genome_node_delete(gn);
   
  gn = *(GtGenomeNode**) gt_array_get(results,3);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "PTEN") == 0);
  
  gt_genome_node_delete(gn);
  
  gt_array_delete(results);
  
  /* Fetch only protein coding */
  
  results = gt_array_new(sizeof (GtFeatureNode*));
  
  biotype_filter[0] = "protein_coding";
  
  gt_ensembl_fetch_genes_for_range(ga,
                                   fi,
                                   results,
                                   "10",
                                   &qry_range,
                                   biotype_filter,
                                   1,
                                   err);
                                                
  gt_ensure(gt_array_size(results) == 3);
  
  gn = *(GtGenomeNode**) gt_array_get(results,0);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "KLLN") == 0);
  
  gt_genome_node_delete(gn);
  
  gn = *(GtGenomeNode**) gt_array_get(results,1);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "ATAD1") == 0);
  
  gt_genome_node_delete(gn);
   
  gn = *(GtGenomeNode**) gt_array_get(results,2);
  
  fn = gt_feature_node_cast(gn);
  
  gt_ensure(strcmp(gt_feature_node_get_attribute(fn, ID), "PTEN") == 0);
  
  gt_genome_node_delete(gn);
  
  gt_array_delete(results);
  
  gt_ensembl_gene_adaptor_delete(ga);
  gt_feature_index_delete(fi);
  gt_anno_db_schema_delete(adb);
  gt_rdb_delete((GtRDB*) rdb);
  
  return had_err;
}
