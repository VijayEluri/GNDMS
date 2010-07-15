# Large amounts of memory ensure a fast build
ENV['JAVA_OPTS'] ||= '-Xms512m -Xmx768m'


# Additional maven repositories 
repositories.remote << 'http://www.ibiblio.org/maven2'
repositories.remote << 'http://guiceyfruit.googlecode.com/svn/repo/releases'
repositories.remote << 'http://download.java.net/maven/2'
repositories.remote << 'http://static.appfuse.org/repository'
repositories.remote << 'http://repository.jboss.org/maven2/'
repositories.remote << 'http://google-maven-repository.googlecode.com/svn/repository/'


# Don't touch below unless you know what you are doing
# --------------------------------------------------------------------------------------------------

VERSION_NUMBER = '0.3-pre'
VERSION_NAME = 'Kylie++'
GROUP_NAME = 'de.zib.gndms'
MF_COPYRIGHT = 'Copyright 2008-2010 Zuse Institute Berlin (ZIB)'
MF_LICENSE ='This software has been licensed to you under the terms and conditions of the Apache License 2.0 (APL 2.0) only.  See META-INF/LICENSE for detailed terms and conditions.'
USERNAME = ENV['USER'].to_s

# Helper to create non-standard GNDMS sub-project layouts
require 'buildr/gndms'
include GNDMS


# Test environment
testEnv('GLOBUS_LOCATION', 'the root directory of Globus Toolkit 4.0.8')
testEnv('ANT_HOME', 'the root directory of Apache Ant')
testEnv('JAVA_HOME', 'the root directory of J2SE 1.6')
JAVA_HOME = ENV['JAVA_HOME']
# ENV['PATH'] = File.join([ENV['JAVA_HOME'], 'bin']) + File::PATH_SEPARATOR + ENV['PATH']
SOURCE = '1.5'
TARGET = '1.5'
testEnv('GNDMS_SOURCE', 'the root directory of GNDMS source distribution (i.e. the toplevel directory in which the Buildfile resides)')
testEnv('USER', 'your user\'s login (your UNIX is weird)')
testTool('rsync')
testTool('curl')
testTool('openssl')
testTool('hostname')
HOSTNAME = `hostname`


# Helper to construct GT4 jar pathes
require 'buildr/gt4'
include GT4

# Groovy support is needed by the monitor
require 'buildr/groovy'


# Essentially GT4 package management is classloading unaware crap
# Therefore we have to filter out some jars in order to avoid invalid jar-shadowing through dependencies
def skipDeps(deps) 
  deps = deps.select { |ding| !ding.include?("/commons-cli") }
  deps = deps.select { |ding| !ding.include?("/commons-logging") }
#  deps = deps.select { |ding| !ding.include?("/commons-lang-2.1") }
  deps = deps.select { |ding| !ding.include?("/commons-pool") }
  return deps
end


# Non-GT4 dependencies
# ACTI = 'javax.activation:activation:jar:1.1.1'
# GOOGLE_COLLECTIONS = 'com.google.collections:google-collections:jar:0.9'
GUICE = 'com.google.code.guice:guice:jar:2.0'
GOOGLE_COLLECTIONS = 'com.google.code.google-collections:google-collect:jar:snapshot-20080530'
JETBRAINS_ANNOTATIONS = 'com.intellij:annotations:jar:7.0.3'
JODA_TIME = transitive('joda-time:joda-time:jar:1.6')
CXF = 'org.apache.cxf:cxf-bundle:jar:2.1.4'
JAXB = 'javax.xml.bind:jaxb-api:jar:2.2.1'
STAX = 'stax:stax-api:jar:1.0.1'
COMMONS_COLLECTIONS = transitive(['commons-collections:commons-collections:jar:3.2.1'])
COMMONS_CODEC = 'commons-codec:commons-codec:jar:1.4'
COMMONS_LANG = 'commons-lang:commons-lang:jar:2.1'
COMMONS_FILEUPLOAD = transitive(['commons-fileupload:commons-fileupload:jar:1.2.1'])
JETTY = ['jetty:jetty:jar:6.0.2', 'jetty:jetty-util:jar:6.0.2']
ARGS4J = 'args4j:args4j:jar:2.0.14'
TESTNG = download(artifact('org.testng:testng:jar:5.1-jdk15') => 'http://static.appfuse.org/repository/org/testng/testng/5.1/testng-5.1-jdk15.jar')
# TESTNG = 'org.testng:testng:jar:5.1'
DB_DERBY = 'org.apache.derby:derby:jar:10.5.3.0'
HTTP_CORE = ['org.apache.httpcomponents:httpcore:jar:4.0', 'org.apache.httpcomponents:httpcore-nio:jar:4.0', 'org.apache.httpcomponents:httpclient:jar:4.0.1']

# Grouped GT4 dependencies
GT4_COMMONS = gt4jars(['commons-beanutils.jar', 
                       'commons-digester.jar',
                       'commons-discovery.jar',
                       'commons-pool.jar'])
GT4_LOG = gt4jars(['commons-logging.jar', 'log4j-1.2.15.jar'])
GT4_COG = gt4jars(['cog-axis.jar', 'cog-jglobus.jar', 'cog-url.jar'])
GT4_AXIS = gt4jars(['axis.jar', 'axis-url.jar', 'saaj.jar'])
GT4_WSRF = gt4jars(['addressing-1.0.jar',
                    'axis-url.jar',
                    'axis.jar',
                    'commonj.jar',
                    'concurrent.jar',
                    'globus_wsrf_rft_stubs.jar',
                    'naming-common.jar',
                    'wsdl4j.jar',
                    'saaj.jar',
                    'wsrf_common.jar',
                    'wsrf_core.jar',
                    'wsrf_core_stubs.jar',
                    'wsrf_tools.jar'])
GT4_SERVLET = gt4jars(['servlet.jar'])
GT4_SEC = gt4jars(['puretls.jar', 'opensaml.jar', 
                   'cryptix-asn1.jar', 'cryptix.jar', 'cryptix32.jar', 
                   'jce-jdk13-125.jar', 'wss4j.jar', 'jgss.jar', 
                   'globus_delegation_service.jar',
                   'globus_delegation_stubs.jar'])
GT4_XML = gt4jars(['xalan-2.6.jar', 'xercesImpl-2.7.1.jar', 'xml-apis.jar', 'xmlsec.jar', 'jaxrpc.jar'])
GT4_GRAM = gt4jars(['gram-monitoring.jar', 'gram-service.jar', 'gram-stubs.jar', 'gram-utils.jar'])


# OpenJPA is required by gndms:model
OPENJPA = transitive('org.apache.openjpa:openjpa:jar:2.0.0')

require 'buildr/openjpa2'
include Buildr::OpenJPA2



desc 'Germanys Next Data Management System'
define 'gndms' do
    project.version = VERSION_NUMBER
    project.group = GROUP_NAME
    manifest['Copyright'] = MF_COPYRIGHT
    manifest['License'] = MF_LICENSE
    compile.options.source = SOURCE
    compile.options.target = TARGET
    # compile.options.lint = 'all'
    meta_inf << file(_('LICENSE'))
    meta_inf << file(_('GNDMS-RELEASE'))
    test.using :testng

    # WSRF GT4 services to be built
    SERVICES = ['GORFX', 'DSpace']
    # TODO: Replace with some ruby magic
    DSPACE_STUBS   = _('services/DSpace/build/lib/gndms-dspace-stubs.jar')
    DSPACE_CLIENT  = _('services/DSpace/build/lib/gndms-dspace-client.jar')
    DSPACE_COMMON  = _('services/DSpace/build/lib/gndms-dspace-common.jar')
    DSPACE_SERVICE = _('services/DSpace/build/lib/gndms-dspace-service.jar')
    DSPACE_TESTS   = _('services/DSpace/build/lib/gndms-dspace-tests.jar')
    GORFX_STUBS   = _('services/GORFX/build/lib/gndms-gorfx-stubs.jar')
    GORFX_CLIENT  = _('services/GORFX/build/lib/gndms-gorfx-client.jar')
    GORFX_COMMON  = _('services/GORFX/build/lib/gndms-gorfx-common.jar')
    GORFX_SERVICE = _('services/GORFX/build/lib/gndms-gorfx-service.jar')
    GORFX_TESTS   = _('services/GORFX/build/lib/gndms-gorfx-tests.jar')
    SERVICE_STUBS = [GORFX_STUBS, DSPACE_STUBS]

    # buildFile = File.new(_('GNDMS-BUILD-INFO'), 'w')
    # timestamp = Time.now.to_s
    # buildFile.syswrite('built-at: ' + timestamp + ' built-by: ' + USERNAME + '@' + HOSTNAME)
    # buildFile.close
    meta_inf << file(_('GNDMS-BUILD-INFO'))

    # versionString = 'Generation N Data Management System VERSION: ' + VERSION_NUMBER + ' "' + VERSION_NAME + '"'
    # relFile = File.new(_('GNDMS-RELEASE'), 'w')
    # relFile.syswrite(versionString)
    # relFile.close
 
    desc 'GT4-independent utility classes for GNDMS'
    define 'stuff', :layout => dmsLayout('stuff', 'gndms-stuff') do
       compile.with GUICE, GOOGLE_COLLECTIONS, JETBRAINS_ANNOTATIONS
       compile
       package :jar
    end

    desc 'Shared database model classes'
    define 'model', :layout => dmsLayout('model', 'gndms-model') do
      # TODO: Better XML
      compile.with project('stuff'), COMMONS_COLLECTIONS, GOOGLE_COLLECTIONS, JODA_TIME, JETBRAINS_ANNOTATIONS, GUICE, CXF, OPENJPA, JAXB, STAX
      compile.enhance do open_jpa_enhance end
      package :jar
    end

    desc 'GT4-dependent utility classes for GNDMS'
    define 'kit', :layout => dmsLayout('kit', 'gndms-kit') do
      compile.with JETTY, COMMONS_FILEUPLOAD, COMMONS_CODEC, project('stuff'), project('model'), JETBRAINS_ANNOTATIONS, GT4_LOG, GT4_COG, GT4_AXIS, GT4_SEC, GT4_XML, JODA_TIME, ARGS4J, GUICE, GT4_SERVLET, COMMONS_LANG, OPENJPA, Buildr::Groovy::Groovyc.dependencies
      compile
      package :jar
    end

    desc 'GNDMS logic classes (actions for manipulating resources)'
    define 'logic', :layout => dmsLayout('logic', 'gndms-logic') do
       compile.with JETBRAINS_ANNOTATIONS, project('kit'), project('stuff'), project('model'), JODA_TIME, GOOGLE_COLLECTIONS, GUICE, DB_DERBY, GT4_LOG, GT4_AXIS, GT4_COG, GT4_SEC, GT4_XML, COMMONS_LANG, OPENJPA, Buildr::Groovy::Groovyc.dependencies
       package :jar
    end

    desc 'GNDMS classes for dealing with wsrf and xsd types'
    define 'gritserv', :layout => dmsLayout('gritserv', 'gndms-gritserv') do
      compile.with JETBRAINS_ANNOTATIONS, project('kit'), project('stuff'), project('model'), ARGS4J, JODA_TIME, GORFX_STUBS, OPENJPA, GT4_LOG, GT4_WSRF, GT4_COG, GT4_SEC, GT4_XML, GT4_COMMONS, COMMONS_LANG, COMMONS_COLLECTIONS, Buildr::Groovy::Groovyc.dependencies
      compile
      package :jar
    end

    desc 'GNDMS core infrastructure classes'
    define 'infra', :layout => dmsLayout('infra', 'gndms-infra') do
      # Infra *must* have all dependencies since we use this list in copy/link-deps
      compile.with JETBRAINS_ANNOTATIONS, OPENJPA, project('gritserv'), project('logic'), project('kit'), project('stuff'), project('model'), ARGS4J, SERVICE_STUBS, JODA_TIME, JAXB, GT4_SERVLET, JETTY, CXF, GOOGLE_COLLECTIONS, GUICE, DB_DERBY, GT4_LOG, GT4_WSRF, GT4_GRAM, GT4_COG, GT4_SEC, GT4_XML, JAXB, GT4_COMMONS, COMMONS_CODEC, COMMONS_LANG, COMMONS_COLLECTIONS, HTTP_CORE, TESTNG
      compile
      package :jar

      # Symlink or copy all dependencies of infra + the infra jar - whatever gets filtered by skipDeps to GT4LIB
      # and log source jars used to lib/DEPENDENCIES and lib/dependencies.xml (both files are not further used by the build - FYI only)
      def installDeps(copy)
        deps = Buildr.artifacts(project('infra').compile.dependencies).map(&:to_s)
        deps << project('infra').package.to_s
        deps = skipDeps(deps)

        classpathFile = File.new(_('../lib/dependencies.xml'), 'w')
        classpathFile.syswrite('<?xml version="1.0"?>' + "\n" + '<project><target id="setGNDMSDeps"><path id="service.build.extended.classpath">' + "\n")
        depsFile = File.new(_('../lib/DEPENDENCIES'), 'w')
        deps.select { |jar| jar[0, GT4LIB.length] != GT4LIB }.each { |file| 
           if (copy)
             puts 'cp: \'' + file + '\' to: \'' + GT4LIB + '\''
             cp(file, GT4LIB)
           else
             puts 'ln_sf: \'' + file + '\' to: \'' + GT4LIB + '\''
             ln_sf(file, GT4LIB)
           end
           depsFile.syswrite(file + "\n") 
           classpathFile.syswrite('<pathelement location="' + file + '" />' + "\n")
        }
        depsFile.close
        classpathFile.syswrite('</path></target></project>' + "\n\n")
        classpathFile.close
      end

      desc 'Symlink dependencies to $GLOBUS_LOCATION/lib'
      task 'link-deps' => :package do
        installDeps(false)
      end

      desc 'Copy dependencies to $GLOBUS_LOCATION/lib'
      task 'copy-deps' => :package do
        installDeps(true)
      end

    end

    desc 'rsync type schemata between services and types'
    task :typesync  => :package do
      SERVICES.each { |service|
        system 'rsync -aurl ' + _('../services' + service + '/build/schema/') + ' ' + _('types/')
        system 'rsync -aurl ' + _('../services' + service + '/schema/') + ' ' + _('types/')
      }
    end

    task 'clean-services' do
      SERVICES.each { |service| 
        system 'cd ' + _('services/'+service) + ' && ant clean'
      }
    end

    task 'package-stubs' do
      SERVICES.each { |service| 
        system 'cd ' + _('services/'+service) + ' && ant jarStubs'
      }
    end

    desc 'Create DSpace GAR for deployment (Requires packaged GNDMS and installed dependencies)'
    task 'package-DSpace' do
      system 'cd ' + _('services/DSpace') + ' && ant createDeploymentGar'
      ln_sf(_('services/DSpace/gndms_DSpace.gar'), _('.'))
    end

    desc 'Create GORFX GAR for deployment (Requires packaged GNDMS and installed dependencies)'
    task 'package-GORFX' do
      system 'cd ' + _('services/GORFX') + ' && ant createDeploymentGar'
      ln_sf(_('services/GORFX/gndms_GORFX.gar'), _('.'))
    end
end

task 'clean-services' => 'gndms:clean-services' do
end

task 'package-stubs' => 'gndms:package-stubs' do
end

task 'install-deps' => 'gndms:infra:copy-deps' do
end

task 'copy-deps' => 'gndms:infra:copy-deps' do
end

task 'link-deps' => 'gndms:infra:link-deps' do
end

task 'package-DSpace' => 'gndms:package-DSpace' do
end

task 'package-GORFX' => 'gndms:package-GORFX' do
end

# task 'pt-install' => ['package-stubs', 'install-deps', 'package-gars'] do
# end
