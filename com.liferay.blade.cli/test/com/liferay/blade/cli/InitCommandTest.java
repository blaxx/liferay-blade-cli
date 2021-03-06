/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import aQute.lib.io.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.gradle.testkit.runner.BuildTask;
import org.junit.After;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class InitCommandTest {

	@After
	public void cleanUp() throws Exception {
		IO.delete(workspaceDir.getParentFile());
	}

	@Test
	public void testMoveLayouttplToWars() throws Exception {
		File testdir = IO.getFile("generated/testMoveLayouttplToWars");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u", "-o"};

		new bladenofail().run(args);

		File layoutWar = new File(projectDir, "wars/1-2-1-columns-layouttpl");

		assertTrue(layoutWar.exists());

		assertFalse(new File(layoutWar, "build.xml").exists());

		assertFalse(new File(layoutWar, "build.gradle").exists());

		assertFalse(new File(layoutWar, "docroot").exists());
	}

	@Test
	public void testMoveThemesToWars() throws Exception {
		File testdir = IO.getFile("generated/testMoveThemesToWar");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u", "-o"};

		new bladenofail().run(args);

		File theme = new File(projectDir, "wars/sample-styled-minimal-theme");

		assertTrue(theme.exists());

		assertFalse(new File(theme, "build.xml").exists());

		assertTrue(new File(theme, "build.gradle").exists());

		assertFalse(new File(theme, "docroot").exists());

		assertTrue(new File(theme, "src/main/webapp").exists());

		assertFalse(new File(theme, "src/main/webapp/_diffs").exists());

		assertFalse(new File(projectDir, "plugins-sdk/themes/sample-styled-minimal-theme").exists());
	}

	@Test
	public void testMovePluginsToWars() throws Exception {
		File testdir = IO.getFile("generated/testMovePluginsToWars");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u", "-o"};

		new bladenofail().run(args);

		File sampleExpandoHook = new File(projectDir, "wars/sample-expando-hook");

		assertTrue(sampleExpandoHook.exists());

		assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-expando-hook").exists());

		File sampleServletFilterHook = new File(projectDir, "wars/sample-servlet-filter-hook");

		assertTrue(sampleServletFilterHook.exists());

		assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-servlet-filter-hook").exists());

		File sampleJspPortlet = new File(projectDir, "wars/sample-jsp-portlet");

		assertTrue(sampleJspPortlet.exists());

		assertFalse(new File(projectDir, "plugins-sdk/portlets/sample-jsp-portlet").exists());

		File sampleDaoPortlet = new File(projectDir, "wars/sample-dao-portlet");

		assertTrue(sampleDaoPortlet.exists());

		assertFalse(new File(projectDir, "plugins-sdk/portlets/sample-dao-portlet").exists());

		contains(
			new File(projectDir, "wars/sample-dao-portlet/build.gradle"),
			".*compile group: 'c3p0', name: 'c3p0', version: '0.9.0.4'.*",
			".*compile group: 'mysql', name: 'mysql-connector-java', version: '5.0.7'.*");

		contains(
			new File(projectDir, "wars/sample-tapestry-portlet/build.gradle"),
			".*compile group: 'hivemind', name: 'hivemind', version: '1.1'.*",
			".*compile group: 'hivemind', name: 'hivemind-lib', version: '1.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-annotations', version: '4.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-framework', version: '4.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-portlet', version: '4.1'.*");

		assertFalse(new File(projectDir, "wars/sample-tapestry-portlet/ivy.xml").exists());
	}

	@Test
	public void testBladeInitUpgradePluginsSDKTo70() throws Exception {
		File testdir = IO.getFile("generated/testUpgradePluginsSDKTo70");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		testdir.mkdirs();

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		File buildProperties = new File(projectDir, "plugins-sdk/build.properties");

		Properties props = new Properties();

		props.load(new FileInputStream(buildProperties));

		String version = props.getProperty("lp.version");

		assertEquals("7.0.0", version);
	}

	@Test
	public void testBladeInitDontLoseGitDirectory() throws Exception {
		File testdir = IO.getFile("generated/testBladeInitDontLoseGitDirectory");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		testdir.mkdirs();

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		File gitdir = IO.getFile(projectDir, ".git");

		assertTrue(gitdir.exists());

		File oldGitIgnore = IO.getFile(projectDir, "plugins-sdk/.gitignore");

		assertTrue(oldGitIgnore.exists());
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init"};

		new bladenofail().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "modules").exists());

		assertFalse(new File(workspaceDir, "com").exists());

		verifyGradleBuild();
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init"};

		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new bladenofail().run(args);

		assertFalse(new File(workspaceDir, "build.gradle").exists());
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init", "-f"};

		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new bladenofail().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "modules").exists());

		verifyGradleBuild();
	}

	@Test
	public void testInitInPluginsSDKDirectory() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init", "-u"};

		makeSDK(workspaceDir);

		new bladenofail().run(args);

		assertTrue((new File(workspaceDir, "build.gradle").exists()));

		assertTrue((new File(workspaceDir, "modules").exists()));

		assertTrue((new File(workspaceDir, "themes").exists()));

		assertFalse((new File(workspaceDir, "portlets").exists()));

		assertFalse((new File(workspaceDir, "hooks").exists()));

		assertFalse((new File(workspaceDir, "build.properties").exists()));

		assertFalse((new File(workspaceDir, "build.xml").exists()));

		assertTrue(
			(new File(workspaceDir, "plugins-sdk/build.properties").exists()));

		assertTrue((new File(workspaceDir, "plugins-sdk/build.xml").exists()));
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {
			"-b", workspaceDir.getPath(), "init", "-f", "newproject"
		};

		assertTrue(new File(workspaceDir, "newproject").mkdirs());

		new bladenofail().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/modules").exists());
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {
			"-b", workspaceDir.getPath(), "init", "newproject"
		};

		assertTrue(new File(workspaceDir, "newproject").mkdirs());

		assertTrue(new File(workspaceDir, "newproject/foo").createNewFile());

		new bladenofail().run(args);

		assertFalse(
			new File(workspaceDir, "newproject/build.gradle").exists());
	}

	@Test
	public void testInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {
			"-b", workspaceDir.getPath(), "init", "newproject"
		};

		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}

		new bladenofail().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/modules").exists());
	}

	private void createBundle(File workspaceDir) throws Exception {
		String projectPath = "generated/test/workspace/modules";

		String[] args = {"create", "-d", projectPath, "foo"};

		new bladenofail().run(args);

		File file = IO.getFile(projectPath + "/foo");
		File bndFile = IO.getFile(projectPath + "/foo/bnd.bnd");

		assertTrue(file.exists());

		assertTrue(bndFile.exists());
	}

	private void verifyGradleBuild() throws Exception{
		createBundle(workspaceDir);

		String projectPath = workspaceDir.getPath() + "/modules";

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspaceDir.getPath(), "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");
	}

	private void makeSDK(File dir) throws IOException {
		assertTrue(new File(dir, "portlets").mkdirs());
		assertTrue(new File(dir, "hooks").mkdirs());
		assertTrue(new File(dir, "layouttpl").mkdirs());
		assertTrue(new File(dir, "themes").mkdirs());
		assertTrue(new File(dir, "build.properties").createNewFile());
		assertTrue(new File(dir, "build.xml").createNewFile());
		assertTrue(new File(dir, "build-common.xml").createNewFile());
		assertTrue(new File(dir, "build-common-plugin.xml").createNewFile());
	}

	private void contains(File file, String... patterns) throws Exception {
		String content = new String(IO.read(file));

		for (String pattern : patterns) {
			contains(content, pattern);
		}
	}

	private void contains(String content, String pattern) throws Exception {
		assertTrue(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}
	private final File workspaceDir = IO.getFile("generated/test/workspace");

}