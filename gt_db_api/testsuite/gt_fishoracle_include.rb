# Segments

Name "gt fishoracle segment test (plain)"
Keywords "gt_fishoracle "
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_plain.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (sorted)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-sorted -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_sorted.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (project 1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_project1.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (project 2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '2' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_project2.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (2 projects)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' '2' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_plain.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (projects 3)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 3 -range 71573858 73547653 " +
           "-projects '3' -lower_th -0.6 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_project3.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (tissue project1) (1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' -tissues '1' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_tissue_project1_1.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (tissue project1) (2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' -tissues '2' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_tissue_project1_2.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (tissue project2) (1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '2' -tissues '1' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_tissue_project2_1.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (tissue project2) (2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '2' -tissues '2' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_tissue_project2_2.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (tissue 2 projects) (1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' '2' -tissues '1' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_tissue_2projects_1.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (tissue 2 projects) (2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' '2' -tissues '2' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_tissue_2projects_2.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (2 tissues 2 projects)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' '2' -tissues '1' '2' -lower_th -0.9 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_plain.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (additional experiments) (1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 10 -range 88986342 90713681 " +
           "-projects '1' -add-experiments '8' '9' -lower_th -0.9 " +
           "-trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_add_exp1.gff3 out_sorted.gff3"
end

Name "gt fishoracle segment test (additional experiments) (2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -seqid 3 -range 71573858 73547653 " +
           "-projects '3' -add-experiments '5' -lower_th -0.6 " +
           "-trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_add_exp2.gff3 out_sorted.gff3"
end

# Mutations
#-------------------------------------------------------------------------------

Name "gt fishoracle mutation test (plain)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_plain.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (project 1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-projects '1' -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_project1.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (project 2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-projects '2' -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_project2.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (2 projects)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-projects '1' '2' -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_plain.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (quality 1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-quality 20.0 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_plain.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (quality 2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-quality 35.0 -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_quality2.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (somatic 1)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-somatic somatic -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_somatic1.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (somatic 2)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-somatic germline -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_somatic2.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (high conf)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-confidence 'high\ confidence' -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_high_conf.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (mod conf)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-confidence 'moderate\ confidence' -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_mod_conf.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (low conf)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-confidence 'low\ confidence' -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_low_conf.gff3 out_sorted.gff3"
end

Name "gt fishoracle mutation test (additional experiment)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch mutations -seqid 10 -range 89500000 91700000 " +
           "-projects '1' -add-experiments '16' -trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_mut_add_exp1.gff3 out_sorted.gff3"
end

# Translocations
#-------------------------------------------------------------------------------

Name "gt fishoracle translocation test (plain)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch translocations -seqid 10 -range 19000000 25000000 " +
           "-trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_transloc_plain.gff3 out_sorted.gff3"
end

Name "gt fishoracle translocation test (processed)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch translocations -process -seqid 10 -range 19000000 25000000 " +
           "-trackid testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_transloc_proc.gff3 out_sorted.gff3"
end

# Generic Features
#-------------------------------------------------------------------------------

Name "gt fishoracle generic feature test (plain)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch generic -generic-type Methylation " + 
           "-seqid 10 -range 89000000 91000000 > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 > out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_generic_plain.gff3 out_sorted.gff3"
end

# Segment status Features
#-------------------------------------------------------------------------------

Name "gt fishoracle segment test (status)"
Keywords "gt_fishoracle"
Test do
  run_test "#{$bin}gt fishoracle -database testoracle -user fotest " +
           "-password 123 -fetch segments -segment-type penncnv -sorted " + 
           "-seqid 10 -range 88986342 91713681 -stati 0 1 2 -trackid " +
           "testtrack > out.gff3"
  run "#{$bin}gt gff3 -sort out.gff3 >out_sorted.gff3"
  run "diff -e #{$testdata}fishoracle/gt_fishoracle_status.gff3 out_sorted.gff3"
end
