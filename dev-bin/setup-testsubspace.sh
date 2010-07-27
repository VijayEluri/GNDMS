#!/bin/sh

STAGING_COMMAND="%{GNDMS_SOURCE}/scripts/ist-staging.sh"
ESTIMATION_COMMAND="%{GNDMS_SOURCE}/scripts/dummy-estimation.sh"
STAGING_AREA_PATH="/tmp/testss"
STAGING_AREA_SIZE="2000000" # Currently unused

source $(dirname $0)/../scripts/internal/echo-hostname.sh
source $(dirname $0)/dir-check.sh
# One can set the $hn variable manually in the check-hostname script,
# if the returned value isn't the desired one.
#hn=csr-pc25.zib.de
hn=$(echo_hostname)
STAGING_AREA_GSI_FTP_URL="gsiftp://$hn$STAGING_AREA_PATH"



# Do not edit below this line unless very sure ---------------------------------------------------------------------------------------------------------------------------------------------------

MODE=$1

[ -z "$MODE" ] && echo "Must specify mode (CREATE; UPDATE; DELETE)!" && exit 1

ADDMODE=ADD
[ "$MODE" = "DELETE" ] && ADDMODE=REMOVE

if [ "$MODE" = "CREATE" ]; then
    dir_check $STAGING_AREA_PATH
fi

# Variables in action parameters denoted using %{VARNAME} will be expanded 
# *at* *runtime* by the globus container using its regular shell environment

# Setup subspace and slicekinds

moni call -v .dspace.SetupSubspace "subspace:'{http://www.c3grid.de/G2/Subspace}TransferSubspace'; path:'$STAGING_AREA_PATH'; gsiFtpPath: '$STAGING_AREA_GSI_FTP_URL'; visible:true; size:'$STAGING_AREA_SIZE'; mode:'$MODE'"
moni call -v .dspace.SetupSliceKind "sliceKind:'http://www.c3grid.de/G2/SliceKind/TransferDst'; sliceKindMode:664; uniqueDirName: testSlices; mode: $MODE"
moni call -v .dspace.AssignSliceKind "subspace:'{http://www.c3grid.de/G2/Subspace}TransferSubspace'; sliceKind: http://www.c3grid.de/G2/SliceKind/TransferDst; mode:'$ADDMODE'"


#for ptgrid tests:
moni call -v .dspace.SetupSliceKind "\
        sliceKind:'http://www.c3grid.de/G2/SliceKind/DMS'; \
        sliceKindMode:500; \
        uniqueDirName: dmsROSlices; \
        mode: '$MODE'"
moni call -v .dspace.AssignSliceKind "\
    subspace:'{http://www.ptgrid.de/G1/Subspace}Staging';\
    sliceKind: http://www.c3grid.de/G2/SliceKind/DMS;\
    mode:'$ADDMODE'"


#moni call -v .gorfx.ConfigOfferType "offerType: 'http://www.c3grid.de/ORQTypes/ProviderStageIn';\
#cfgOutFormat: 'PRINT_OK'; subspace: '{http://www.c3grid.de/G2/Subspace}SubspaceTests';\
#sliceKind: 'http://www.c3grid.de/G2/SliceKind/Staging';\
#stagingClass: 'de.zib.gndms.logic.model.gorfx.c3grid.ExternalProviderStageInAction';\
#estimationClass: 'de.zib.gndms.logic.model.gorfx.c3grid.ExternalProviderStageInORQCalculator';\
#stagingCommand='$STAGING_COMMAND';\
#estimationCommand='$ESTIMATION_COMMAND'"
# vim:tw=0