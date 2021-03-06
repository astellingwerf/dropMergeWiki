import com.opentext.dropmerge.dsl.DropMergeInput

class CWSTransformerProvider {

    public static void main(String[] args) {
        DropMergeInput.provide {
            team {
                name 'CWS'
                productManager 'hkastenb'
                architect 'rprins', 'awisse'
                scrumMaster 'rprins'
            }

            wiki {
                userName myProperties['wikiUserName']
                password myProperties['wikiPassword']
                pageId myProperties['wikiDropMergePageId']
            }

            crucible {
                userName myProperties['crucibleUserName']
                password myProperties['cruciblePassword']
                projectKey 'CWS'
            }

            jenkins {
                regressionTests {
                    ofType('JUnit') {
                        withJob { job 'cws-wip-junit-l' on buildMasterNL; description 'Linux' }
                        comparedToJob { job 'CWS-L' on jenkinsOfSVT; description 'Linux' }
                        andJob { job 'CWSOldBuild-L' on jenkinsOfSVT; description 'Linux' }
                        andJob { job 'CAP-CWS-L' on jenkinsOfSVT; description 'Linux' }

                        withJob { job 'cws-wip-junit-w' on buildMasterNL; description 'Windows' }
                        comparedToJob { job 'CWS-W' on jenkinsOfSVT; description 'Windows' }
                        andJob { job 'CWSOldBuild-W' on jenkinsOfSVT; description 'Windows' }
                        andJob { job 'CAP-CWS-W' on jenkinsOfSVT; description 'Windows' }
                    }
                    ofType('CWS UIUnits') {
                        withJob {
                            on buildMasterNL job 'cws-wip-uiunit' matrixValues browser: 'Chrome';
                            description 'Chrome'
                        }
                        withJob {
                            on buildMasterNL job 'cws-wip-uiunit' matrixValues browser: 'Firefox';
                            description 'Firefox'
                        }
                    }
                    ofType('GMF UIUnits Runtime Ref') {
                        withJob {
                            on buildMasterNL job 'cws-wip-uiunit-runtime-ref-test' matrixValues browser: 'Chrome';
                            description 'Chrome'
                        }
                        withJob {
                            on buildMasterNL job 'cws-wip-uiunit-runtime-ref-test' matrixValues browser: 'Firefox';
                            description 'Firefox'
                        }
                    }
                    ofType('GMF UIUnits') {
                        withJob { on buildMasterNL job 'cws-wip-gmf-chrome'; description 'Chrome' }
                        withJob { on buildMasterNL job 'cws-wip-gmf-ff'; description 'Firefox' }
                    }
                }
                upgrade {
                    withJob { on buildMasterNL job 'A_UP_4.1CU7.1-WIP_W'; description 'on Windows from BOP 4.1 CU7.1' }
                    withJob { on buildMasterNL job 'B_UP_4.1CU6-4.1CU7.1_L'; description 'on Linux from BOP 4.1 CU6 via BOP 4.1 CU7.1' }
                    withJob { on buildMasterNL job 'C_UP_4.1CU7.1-WIP_L'; description 'on Linux from BOP 4.1 CU7.1' }
                    withJob { on buildMasterNL job 'D_UP_4.2CU1-WIP_L'; description 'on Linux from BOP 4.2 CU1' }
                    withJob { on buildMasterNL job 'E_UP_4.1CU7.1-WIP_W'; description 'on Windows from BOP 4.1 CU7.1' }
                    withJob { on buildMasterNL job 'F_UP_4.2CU1-WIP_W'; description 'on Windows from BOP 4.2 CU1' }
                    withJob { on buildMasterNL job 'wip-content-upgrade'; description 'to test content migration' }
                }
                integrationTests {
                    withJob { job 'cws-wip-uiunit-EW-NewBuildEngine' on buildMasterNL }
                    withJob { job 'cws-wip-smoketest' on buildMasterNL }
                }
            }
        }
    }
}