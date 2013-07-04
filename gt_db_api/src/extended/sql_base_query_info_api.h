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

#ifndef BASE_QUERY_INFO_API_H
#define BASE_QUERY_INFO_API_H

typedef struct GtBaseQueryInfo GtBaseQueryInfo;

#include "core/error_api.h"
#include "core/array_api.h"
#include "core/str_api.h"

GtBaseQueryInfo* gt_base_query_info_new(GtArray *columns,
                                          GtStr *table,
                                          GtArray *left_joins);

GtStr* gt_base_query_info_get_base_query(GtBaseQueryInfo *bqi);
GtStr* gt_base_query_info_get_left_join(GtBaseQueryInfo *bqi);

GtArray* gt_base_query_info_get_columns(GtBaseQueryInfo *bqi);
GtStr* gt_base_query_info_get_table(GtBaseQueryInfo *bqi);

void gt_base_query_info_delete(GtBaseQueryInfo *bqi);

int gt_base_query_info_unit_test(GtError *err);

#endif
