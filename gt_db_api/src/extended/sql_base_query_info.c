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
#include "extended/sql_base_query_info_api.h"
#include "core/ma.h"
#include "core/array_api.h"
#include "core/str_api.h"
#include "core/ensure.h"

struct GtBaseQueryInfo {
  GtArray *columns;
  GtStr *table;
  GtArray *left_joins; /* two dimensional array */
};

GtBaseQueryInfo* gt_base_query_info_new(GtArray *columns,
                                        GtStr *table,
                                        GtArray *left_joins)
{
  GtBaseQueryInfo *bqi = gt_malloc(sizeof *bqi);
  bqi->columns = columns;
  bqi->table = table;
  bqi->left_joins = left_joins;
  return bqi;
}

GtStr* gt_base_query_info_get_left_join(GtBaseQueryInfo *bqi)
{
  GtStr *qry  = gt_str_new();
  int i;
  GtArray *inner_arr;
  for(i = 0; i < gt_array_size(bqi->left_joins); i++)
  {
	gt_str_append_cstr(qry, " LEFT JOIN ");
	inner_arr = *(GtArray**) gt_array_get(bqi->left_joins, i);
	
    gt_str_append_str(qry, *(GtStr**) gt_array_get(inner_arr, 0));
      
    gt_str_append_cstr(qry, " ON ");
      
    gt_str_append_str(qry, *(GtStr**) gt_array_get(inner_arr, 1));
  }
  
  return qry;
}

GtStr* gt_base_query_info_get_base_query(GtBaseQueryInfo *bqi)
{
  GtStr *qry  = gt_str_new();
  int i;
  
  gt_str_append_cstr(qry, "SELECT ");
  
  for(i = 0; i < gt_array_size(bqi->columns); i++)
  {
    gt_str_append_str(qry, *(GtStr**) gt_array_get(bqi->columns, i));
    if(i + 1 != gt_array_size(bqi->columns))
    {
      gt_str_append_cstr(qry, ", ");
    }
  }
  
  gt_str_append_cstr(qry, " FROM ");
  gt_str_append_str(qry, bqi->table);
  
  GtStr *left_joins = gt_base_query_info_get_left_join(bqi);
  
  gt_str_append_str(qry, left_joins);
  
  gt_str_delete(left_joins);
  
  return qry;
}

GtArray* gt_base_query_info_get_columns(GtBaseQueryInfo *bqi){

  return bqi->columns;
}

GtStr* gt_base_query_info_get_table(GtBaseQueryInfo *bqi){

  return bqi->table;
}

void gt_base_query_info_delete(GtBaseQueryInfo *bqi)
{
  if (!bqi) return;
  int i,j;
  for(i = 0; i < gt_array_size(bqi->columns); i++)
  {
    gt_str_delete( *(GtStr**) gt_array_get(bqi->columns, i));
  }
  gt_array_delete(bqi->columns);
  gt_str_delete(bqi->table);
  
  GtArray *inner_arr;
  
  for(i = 0; i < gt_array_size(bqi->left_joins); i++)
  {
	inner_arr = *(GtArray**) gt_array_get(bqi->left_joins, i);
	for(j = 0; j < gt_array_size(inner_arr); j++)
	{
      gt_str_delete(*(GtStr**) gt_array_get(inner_arr, j));
	}
    gt_array_delete(inner_arr);
  }
  gt_array_delete(bqi->left_joins);
  gt_free(bqi);
}

int gt_base_query_info_unit_test(GtError *err)
{
  
  int had_err = 0;
  
  gt_error_check(err);
  
  GtStr *gene_id, *seq_region, *start, *end, *xref_label;
  gene_id = gt_str_new_cstr("gene.gene_id");
  seq_region = gt_str_new_cstr("gene.seq_region");
  start = gt_str_new_cstr("gene.seq_region_start");
  end = gt_str_new_cstr("gene.seq_region_end");
  xref_label = gt_str_new_cstr("xref.xref_label");
  
  GtArray *columns = gt_array_new(sizeof (GtStr*));
  
  gt_array_add(columns, gene_id);
  gt_array_add(columns, seq_region);
  gt_array_add(columns, start);
  gt_array_add(columns, end);
  gt_array_add(columns, xref_label);
  
  GtStr *table;
  table = gt_str_new_cstr("gene");
  
  GtStr *lftable, *lfcond;
  
  lftable = gt_str_new_cstr("xref");
  
  lfcond = gt_str_new_cstr("gene.xref_id = xref.xref_id");
  
  GtArray *lf;
  
  lf = gt_array_new(sizeof (GtStr*));
  
  gt_array_add(lf, lftable);
  gt_array_add(lf, lfcond);
  
  GtArray *left_joins = gt_array_new(sizeof (GtArray*));
  
  gt_array_add(left_joins, lf);
  
  GtBaseQueryInfo *bqi;
  
  bqi = gt_base_query_info_new(columns, table, left_joins);
  
  /* Test left joins... */
  GtStr *left_join = gt_base_query_info_get_left_join(bqi);
  
  char *expected_left_join = " LEFT JOIN xref ON gene.xref_id = xref.xref_id";
  
  //printf("Expected: \"%s\" \n", expected_left_join);
  //printf("Got: \"%s\" \n", gt_str_get(left_join));
  
  gt_ensure(strcmp(gt_str_get(left_join), expected_left_join) == 0);
  
  /* Test base query... */
  GtStr *base_qry = gt_base_query_info_get_base_query(bqi);
  
  char *expected_base_qry = "SELECT gene.gene_id, gene.seq_region, "
                             "gene.seq_region_start, gene.seq_region_end, "
                             "xref.xref_label FROM gene LEFT JOIN xref ON "
                             "gene.xref_id = xref.xref_id";
                             
  //printf("Expected: \"%s\" \n", expected_base_qry);
  //printf("_____Got: \"%s\" \n", gt_str_get(base_qry));
  
  gt_ensure(strcmp(gt_str_get(base_qry), expected_base_qry) == 0);
  
  gt_str_delete(left_join);
  gt_str_delete(base_qry);
  
  gt_base_query_info_delete(bqi);
  
  return had_err;
}
