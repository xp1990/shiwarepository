#!/usr/bin/perl -w
use strict;
use File::Basename;

my $GROUP_ID = "uk.ac.wmin.cpc.shiwa-repo";
my $VERSION_ID = "2.0.0";

foreach my $file(@ARGV) {
  (my $ART_ID = $file) =~ s/\.[^.]+$//;

  my $cmd = "mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=$file -DgroupId=$GROUP_ID -DartifactId=$ART_ID -Dversion=$VERSION_ID -Dpackaging=jar -DgeneratePom=true";
#-DlocalRepositoryPath=/tmp/repo
  my $out = `$cmd`;
  print $out;
}
