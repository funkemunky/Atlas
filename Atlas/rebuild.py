#!/usr/bin/env python
from __future__ import print_function
import subprocess
import re
import stat
import xml
import sys
import datetime
import time
import argparse
import os
import xml.etree.ElementTree as ElementTree
import copy
import os.path
import io
import shutil
def contains_whole_word(text, word):
    pattern = re.compile(r'\b({0})\b'.format(word), flags=re.IGNORECASE)
    search_result = pattern.search(text)
    return search_result is not None
def remove_empty_lines(text):
    lines = text.split("\n")
    filtered_lines = filter(lambda line:
                            line.strip() != '',
                            lines)
    return "\n".join(filtered_lines)
def differ(text1, text2, trim):
    if trim:
        text1 = trim_text(text1)
        text2 = trim_text(text2)
    return text1 != text2
def trim_text(text):
    lines = text.split("\n")
    trimmed_lines = [line.strip() for line in lines]
    trimmed_text = "\n".join(trimmed_lines)
    return remove_empty_lines(trimmed_text)
def utf_to_stdout(utf_string):
    if (sys.stdout.encoding is not None) and (sys.stdout.encoding != 'utf-8'):
        return utf_string.encode('utf').decode(sys.stdout.encoding, 'replace')
    return utf_string
class GitGateway():
    def get_local_changed_files(self, abs_path, ignore_unversioned=True):
        result = set()
        status_info = invoke(['git', 'status', '-s'], abs_path, ['?'])
        if ignore_unversioned:
            changed_files = self.parse_changed_files(status_info, abs_path, ['?'])
        else:
            changed_files = self.parse_changed_files(status_info, abs_path)
        result.update(changed_files)
        return list(result)
    def parse_changed_files(self, diff_info, abs_path, ignored_statuses=None):
        result = []
        lines = diff_info.split('\n')
        for line in lines:
            file_info = re.split(r'\s+', line.strip(), 1)
            if len(file_info) != 2:
                continue
            if ignored_statuses:
                status = file_info[0]
                suitable = False
                for singe_status in status:
                    if singe_status not in ignored_statuses:
                        suitable = True
                        break
                if not suitable:
                    continue
            file_path = file_info[1]
            result.append(os.path.join(abs_path, file_path))
        return result
    def get_revision_changed_files(self, abs_path, from_revision, to_revision):
        diff_info = invoke(
            ['git', 'diff', '--name-status', from_revision, to_revision], abs_path)
        return self.parse_changed_files(diff_info, abs_path)
    def get_revision(self, project_path):
        return invoke(['git', 'rev-parse', 'HEAD'], project_path).strip()
def is_git_repo(project_path):
    git_files_path = os.path.join(project_path, '.git')
    return os.path.isdir(git_files_path) or check_call('git rev-parse --git-dir', project_path)
class SvnGateway():
    def get_local_changed_files(self, abs_path, ignore_unversioned=True):
        status_info = invoke(['svn', 'status', '--xml', abs_path])
        entries_xpath = '*/entry'
        found_results = find_in_string(status_info, [entries_xpath])
        entries = found_results[entries_xpath]
        return self.svn_xml_status_to_files(entries, abs_path, ignore_unversioned)
    def svn_status_to_files(self, all_lines):
        result = []
        for line in all_lines:
            if os.name != 'nt':
                if not ('/' in line):
                    continue
                file_path = line[line.index("/"):]
            else:
                if not ('\\' in line):
                    continue
                file_path = line[line.index(':\\') - 1:]
            result.append(file_path)
        return result
    def svn_xml_status_to_files(self, found_entries, abs_path, ignore_unversioned=True):
        result = []
        if found_entries:
            if not isinstance(found_entries, list):
                found_entries = [found_entries]
            for entry in found_entries:
                status_info = entry['wc-status']
                status = status_info['item']
                if ignore_unversioned and (status == 'unversioned'):
                    continue
                path = entry['path']
                if status in ['normal', 'modified']:
                    full_path = os.path.join(abs_path, path)
                    if os.path.isdir(full_path):
                        continue
                result.append(path)
        return result
    def svn_xml_diff_to_files(self, paths, abs_path):
        result = []
        if paths:
            if not isinstance(paths, list):
                paths = [paths]
            for path in paths:
                status = path['item']
                path = path['text']
                if status in ['none', 'modified']:
                    full_path = os.path.join(abs_path, path)
                    if os.path.isdir(full_path):
                        continue
                result.append(path)
        return result
    def get_revision_changed_files(self, abs_path, from_revision, to_revision):
        status_info = invoke(
            ['svn', 'diff', '--summarize', '--xml', '-r' + from_revision + ':' + to_revision, abs_path])
        entries_xpath = '*/path'
        found_results = find_in_string(status_info, [entries_xpath])
        entries = found_results[entries_xpath]
        return self.svn_xml_diff_to_files(entries, abs_path)
    def get_revision(self, project_path):
        svn_info = invoke(['svn', 'info', project_path])
        info_lines = svn_info.split('\n')
        revision_prefix = 'Revision: '
        for line in info_lines:
            if line.startswith(revision_prefix):
                return line[len(revision_prefix):]
        raise Exception("Couldn't get svn revision in " + project_path)
def is_svn_repo(path):
    return check_call('svn info', path)
class Project(object):
    version = ""
    artifact_id = ""
    group = ""
    path = None
    def __init__(self, artifact_id, group, version, path):
        self.version = version
        self.artifact_id = artifact_id
        self.group = group
        self.path = path
    def __str__(self):
        return "{}:{}:{}".format(self.group, self.artifact_id, self.version)
    def get_path(self):
        return self.path
    def __eq__(self, other):
        if isinstance(other, self.__class__):
            return (self.path == other.path) and (self.artifact_id == other.artifact_id)
        return NotImplemented
    def __ne__(self, other):
        if isinstance(other, self.__class__):
            return not self.__eq__(other)
        return NotImplemented
    def __hash__(self):
        return (hash(self.path) * 13) + hash(self.artifact_id)
def to_strings(value):
    if isinstance(value, list) or isinstance(value, set):
        return [str(x) for x in value]
    raise Exception("This collection type is not yet implemented")
def as_list(obj):
    result = []
    if isinstance(obj, list):
        result.extend(obj)
    elif not obj is None:
        result.append(obj)
    return result
def invoke(command, work_dir=".", exit_on_failure=False):
    command = prepare_command(command)
    shell = requires_shell()
    p = subprocess.Popen(command,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.PIPE,
                         cwd=work_dir,
                         shell=shell)
    (output_bytes, error_bytes) = p.communicate()
    output = output_bytes.decode('utf-8')
    error = error_bytes.decode('utf-8')
    result_code = p.returncode
    if result_code != 0:
        message = 'Execution failed with exit code ' + str(result_code)
        if not exit_on_failure:
            print(message)
        print(utf_to_stdout(output))
        if error:
            print(" --- ERRORS ---:")
            print(utf_to_stdout(error))
        if exit_on_failure:
            sys.exit(result_code)
        else:
            raise Exception(message)
    if error:
        print("WARN! Error output wasn't empty, although the command finished with code 0!")
    return output
def check_call(command, work_dir="."):
    command = prepare_command(command)
    shell = requires_shell()
    p = subprocess.Popen(command,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.PIPE,
                         cwd=work_dir,
                         shell=shell)
    p.communicate()
    result_code = p.returncode
    return result_code == 0
def invoke_attached(command, work_dir="."):
    command = prepare_command(command)
    shell = requires_shell()
    p = subprocess.Popen(command,
                         stderr=subprocess.STDOUT,
                         cwd=work_dir,
                         shell=shell)
    p.communicate()
    result_code = p.returncode
    if result_code != 0:
        sys.exit(result_code)
def requires_shell():
    return os.name == 'nt'  # on windows commands like mvn won't work without shell
def prepare_command(command):
    if isinstance(command, str):
        return command.split()
    return command
def find_in_file(xml_path, x_paths, ignore_namespaces=True):
    """
    :type xml_path: str
    :type x_paths: list
    :type ignore_namespaces: bool
    :rtype: dict
    """
    tree = ElementTree.parse(xml_path)
    root = tree.getroot()
    return find_in_tree(root, x_paths, ignore_namespaces)
def find_in_string(xml_string, x_paths, ignore_namespaces=True):
    root_element = ElementTree.fromstring(xml_string)
    return find_in_tree(root_element, x_paths, ignore_namespaces)
def find_in_tree(root_element, x_paths, ignore_namespaces):
    elements_dict = gather_elements(root_element, x_paths, ignore_namespaces)
    result = {}
    for x_path, elements in elements_dict.items():
        if (elements is not None) and elements:
            if len(elements) == 1:
                result[x_path] = read_element(elements[0])
            else:
                result[x_path] = [read_element(element) for element in elements]
        else:
            result[x_path] = None
    return result
def gather_elements(root_element, x_paths, ignore_namespaces):
    root_ns = namespace(root_element)
    ns = {}
    if root_ns and ignore_namespaces:
        ns["x"] = root_ns
    result = {}
    for x_path in x_paths:
        search_path = x_path
        if root_ns and ignore_namespaces:
            search_path = adapt_namespace(x_path, "x")
        elements = root_element.findall(search_path, ns)
        if (elements is not None) and elements:
            result[x_path] = elements
        else:
            result[x_path] = None
    return result
def read_element(element):
    attributes_map = dict(element.attrib)
    sub_elements = list(element)
    if len(sub_elements) > 0:
        as_map = {}
        for sub_element in sub_elements:
            key = sub_element.tag
            key = key[key.rfind("}") + 1:]
            value = read_element(sub_element)
            if (key in as_map):
                if isinstance(as_map[key], list):
                    value_list = as_map[key]
                else:
                    value_list = [as_map[key]]
                    as_map[key] = value_list
                value_list.append(value)
            else:
                as_map[key] = value
        if attributes_map:
            as_map.update(attributes_map)
        return as_map
    else:
        if element.text:
            if attributes_map:
                attributes_map['text'] = element.text.strip()
                return attributes_map
            return element.text.strip()
        else:
            if attributes_map:
                return attributes_map
            return ''
def namespace(element):
    m = re.match('\{(.*)\}', element.tag)
    return m.group(1) if m else ''
def adapt_namespace(x_path, prefix):
    path_elements = x_path.split("/")
    result = []
    for element in path_elements:
        if element == '*':
            result.append(element)
            continue
        result.append(prefix + ":" + element)
    return "/".join(result)
def replace_in_tree(file_path, replace_dict, ignore_namespaces=True):
    tree = ElementTree.parse(file_path)
    elements_dict = gather_elements(tree.getroot(), replace_dict.keys(), ignore_namespaces)
    for xpath, elements in elements_dict.items():
        if elements is None:
            continue
        value = replace_dict[xpath]
        if isinstance(elements, list):
            for element in elements:
                element.text = value
        else:
            elements.text = value
    tree.write(file_path)
def parse_options():
    parser = argparse.ArgumentParser(description="Rebuild of complex maven projects.")
    parser.add_argument("-r", "--root_path", help="path to the root project", default=".")
    parser.add_argument("-m", "--maven", help="maven parameters to pass to mvn command", default="")
    parser.add_argument("-o", "--root_only", help="skip projects, which are not submodules of root project hierarchy",
                        action='store_true')
    parser.add_argument("-t", "--track_unversioned", help="also consider local changes in unversioned files",
                        action='store_true')
    parser.add_argument("-c", "--vcs", help="version control system", choices=['svn', 'git'])
    args = vars(parser.parse_args())
    if args["root_path"]:
        root_project_path = args["root_path"]
    else:
        root_project_path = "."
    mvn_opts = args["maven"]
    root_only = args["root_only"]
    track_unversioned = args["track_unversioned"]
    root_project_path = normalize_path(root_project_path)
    print("Root project path: " + root_project_path)
    print("Additional maven arguments: " + str(mvn_opts))
    root_pom_path = os.path.join(root_project_path, "pom.xml")
    if not os.path.exists(root_pom_path):
        print("ERROR! No root pom.xml find in path", os.path.abspath(root_project_path))
        sys.exit(1)
    if args['vcs']:
        if args['vcs'] == 'git':
            vcs_gateway = GitGateway()
        else:
            vcs_gateway = SvnGateway()
    else:
        if is_svn_repo(root_project_path):
            vcs_gateway = SvnGateway()
        elif is_git_repo(root_project_path):
            vcs_gateway = GitGateway()
        else:
            print("Couldn't resolve VCS type, please specify it explicitly using -c argument")
            sys.exit(-1)
    if '-Dmaven.repo.local=' in mvn_opts:
        mvn_repo_path = get_arg_value(mvn_opts, '-Dmaven.repo.local')
    else:
        mvn_repo_path = def_repo_path()
    return (root_project_path, mvn_repo_path, mvn_opts, root_only, track_unversioned, vcs_gateway)
def get_arg_value(options_string, argument):
    pattern = re.compile(argument + '(\s+|=)(.*)')
    for match in pattern.finditer(options_string):
        opts_end = match.group(2)
        if opts_end.startswith('"'):
            return opts_end[1:opts_end.index('"', 1)]
        elif opts_end.startswith("'"):
            return opts_end[1:opts_end.index("'", 1)]
        else:
            return re.split('[\s\b]', opts_end)[0]
def to_mvn_projects(pom_paths, root_path, root_only):
    projects = []
    for pom_path in pom_paths:
        if root_only:
            project_path = os.path.dirname(pom_path)
            project_root_path = get_project_root_path(project_path)
            if project_root_path != root_path:
                project_relative_path = os.path.relpath(project_path, root_path)
                print(project_relative_path + ' is not in root hierarchy. Skipping...')
                continue
        project = create_project(pom_path)
        projects.append(project)
    return projects
def modification_date(file_path):
    time_string = time.ctime(os.path.getmtime(file_path))
    return datetime.datetime.strptime(time_string, "%a %b %d %H:%M:%S %Y")
def deletion_date(file_path):
    path = file_path
    while not os.path.exists(path):
        path = os.path.dirname(path)
        if is_root(path):
            raise Exception("Couldn't find parent folder for the deleted file " + file_path)
    return modification_date(path)
def is_root(path):
    return os.path.dirname(path) == path
def normalize_path(path_string):
    result = os.path.expanduser(path_string)
    if not os.path.isabs(result):
        result = os.path.abspath(result)
    return os.path.normpath(result)
def read_file(filename):
    path = normalize_path(filename)
    file_content = ""
    with open(path, "r") as f:
        file_content += f.read()
    return file_content
def write_file(filename, content):
    path = normalize_path(filename)
    if not os.path.exists(os.path.dirname(path)):
        os.makedirs(os.path.dirname(path))
    with open(path, "w") as file:
        file.write(content)
def make_executable(filename):
    st = os.stat(filename)
    os.chmod(filename, st.st_mode | stat.S_IEXEC)
def exists(filename):
    path = normalize_path(filename)
    return os.path.exists(path)
def last_modification(folder_paths):
    result = None
    for root_folder_path in folder_paths:
        file_date = modification_date(root_folder_path)
        if (result is None) or (result < file_date):
            result = file_date
        for root, subdirs, files in os.walk(root_folder_path):
            for file in files:
                file_path = os.path.join(root, file)
                file_date = modification_date(file_path)
                if (result is None) or (result < file_date):
                    result = file_date
            for folder in subdirs:
                folder_path = os.path.join(root, folder)
                folder_date = modification_date(folder_path)
                if (result is None) or (result < folder_date):
                    result = folder_date
    return result
def equal(path1, path2):
    if not os.path.exists(path1) or not os.path.exists(path2):
        return False
    if os.path.getsize(path1) != os.path.getsize(path2):
        return False
    buf1 = bytearray(4096)
    buf2 = bytearray(4096)
    try:
        file1 = io.open(path1, "rb", 0)
        file2 = io.open(path2, "rb", 0)
        while True:
            numread1 = file1.readinto(buf1)
            numread2 = file2.readinto(buf2)
            if numread1 != numread2:
                return False
            if not numread1:
                break
            if buf1 != buf2:
                return False
    finally:
        if file1:
            file1.close()
        if file2:
            file2.close()
    return True
def prepare_folder(folder_path):
    path = normalize_path(folder_path)
    if not os.path.exists(path):
        os.makedirs(path)
class MavenProject(Project):
    pom_path = None
    packaging = None
    build_directory = None
    source_folders = None
    artifact_name = None
    def __init__(self, artifact_id, group, version, pom_path):
        Project.__init__(self,
                               artifact_id,
                               group,
                               version,
                               os.path.dirname(pom_path))
        self.pom_path = pom_path
    def get_pom_path(self):
        return self.pom_path
    def set_packaging(self, value):
        self.packaging = value
    def get_packaging(self):
        return self.packaging
    def get_build_directory(self):
        return self.build_directory
    def set_build_directory(self, value):
        self.build_directory = value
    def get_source_folders(self):
        return self.source_folders
    def set_source_folders(self, value):
        self.source_folders = value
    def set_artifact_name(self, artifact_name):
        self.artifact_name = artifact_name
    def get_artifact_name(self):
        return self.artifact_name
def create_project(pom_path):
    xml_values = find_in_file(pom_path,
                                        ["artifactId",
                                         "version",
                                         "parent/version",
                                         "groupId",
                                         "parent/groupId",
                                         "packaging",
                                         "build/directory",
                                         "build/sourceDirectory",
                                         "build/testSourceDirectory",
                                         "build/resources/resource/directory",
                                         "build/testResources/testResource/directory",
                                         "build/finalName"
                                         ])
    artifact_id = xml_values["artifactId"]
    version = xml_values["version"]
    if not version:
        version = xml_values["parent/version"]
    group_id = xml_values["groupId"]
    if not group_id:
        group_id = xml_values["parent/groupId"]
    project = MavenProject(artifact_id, group_id, version, pom_path)
    if xml_values["packaging"]:
        project.set_packaging(xml_values["packaging"])
    else:
        project.set_packaging("jar")
    if xml_values["build/directory"]:
        project.set_build_directory(xml_values["build/directory"])
    else:
        project.set_build_directory("target")
    if xml_values["build/finalName"]:
        artifact_name = xml_values["build/finalName"]
        artifact_name += '.' + project.get_packaging()
    else:
        artifact_name = get_default_artifact_name(project)
    project.set_artifact_name(artifact_name)
    source_folders = collect_source_folders(pom_path, xml_values)
    project.set_source_folders(source_folders)
    return project
def collect_source_folders(pom_path, xml_values):
    source_folders = set()
    def add_source_folder(xpath, def_value):
        value = xml_values[xpath]
        if not value:
            value = def_value
        if isinstance(value, list):
            source_folders.update(value)
        elif value:
            source_folders.add(value)
    add_source_folder('build/sourceDirectory', os.path.join('src', 'main', 'java'))
    add_source_folder('build/testSourceDirectory', os.path.join('src', 'test', 'java'))
    add_source_folder('build/resources/resource/directory', os.path.join('src', 'main', 'resources'))
    add_source_folder('build/testResources/testResource/directory', os.path.join('src', 'test', 'resources'))
    result = [pom_path]
    project_path = os.path.dirname(pom_path)
    for folder in source_folders:
        if not os.path.isabs(folder):
            folder = os.path.join(project_path, folder)
        if os.path.exists(folder):
            result.append(folder)
    return result
def repo_artifact_path(project, repo_path):
    artifact_path = repo_folder_path(project, repo_path)
    artifact_name = get_default_artifact_name(project)
    return os.path.join(artifact_path, artifact_name)
def get_default_artifact_name(project):
    """
    :type project: MavenProject
    """
    package_type = project.get_packaging()
    artifact_name = "{}-{}.{}".format(project.artifact_id, project.version, package_type)
    return artifact_name
def repo_pom_path(project, repo_path):
    artifact_path = repo_folder_path(project, repo_path)
    return os.path.join(artifact_path, "{}-{}.pom".format(project.artifact_id, project.version))
def repo_folder_path(project, repo_path):
    folder_path = repo_path
    sub_folders = project.group.split(".")
    for sub_folder in sub_folders:
        folder_path = os.path.join(folder_path, sub_folder)
    folder_path = os.path.join(folder_path, project.artifact_id)
    folder_path = os.path.join(folder_path, project.version)
    return folder_path
def rebuild(parent_project_path, projects, mvn_opts, silent=True):
    if not projects:
        print("No projects to build, skipping")
        return None
    project_roots = analyze_project_roots(projects)
    levels = []
    if len(set(project_roots.values())) > 1:
        dependency_levels = split_by_dependencies(projects, project_roots)
        print("Complex structure detected. Rebuilding in several steps")
        for i in range(0, len(dependency_levels)):
            level = dependency_levels[i]
            grouped_by_root = {}
            for project in level:
                root = project_roots[project]
                if not (root in grouped_by_root):
                    grouped_by_root[root] = []
                grouped_by_root[root].append(project)
            print("Step " + str(i) + ":")
            for root_path, child_projects in grouped_by_root.items():
                rel_root_path = os.path.relpath(root_path, parent_project_path)
                print('\t' + rel_root_path + ': ' + str(to_strings(child_projects)))
                levels.append((root_path, child_projects))
    else:
        root_value = list(project_roots.values())[0]
        levels.append((root_value, project_roots.keys()))
    for root_path, child_projects in levels:
        project_names = [(":" + project.artifact_id) for project in child_projects]
        project_names_string = ",".join(project_names)
        root_pom_path = os.path.join(root_path, 'pom.xml')
        command = 'mvn clean install -f {} {} -pl {}'.format(root_pom_path, mvn_opts, project_names_string)
        if silent:
            invoke(command, parent_project_path, exit_on_failure=True)
        else:
            invoke_attached(command, parent_project_path)
def rebuild_root(parent_project_path, mvn_opts, silent=True):
    command = "mvn clean install " + mvn_opts
    if silent:
        invoke(command, parent_project_path, exit_on_failure=True)
    else:
        invoke_attached(command, parent_project_path)
def split_by_dependencies(projects, project_roots):
    short_names = {}
    for project in projects:
        short_names[project.group + ":" + project.artifact_id] = project
    project_dependencies = {}
    for project in projects:
        dependencies = get_direct_dependencies(project)
        project_dependencies[project] = []
        for dependency in dependencies:
            short_name = dependency["groupId"] + ":" + dependency["artifactId"]
            if short_name in short_names:
                project_dependencies[project].append(short_names[short_name])
    remaining_projects = copy.copy(projects)
    levels = []
    while remaining_projects:
        current_level = []
        remaining_copy = copy.copy(remaining_projects)
        for project in remaining_copy:
            dependencies = project_dependencies[project]
            if dependencies:
                continue
            current_level.append(project)
            remaining_projects.remove(project)
        if not current_level:
            raise IncorrectConfigException(
                "Couldn't build dependency sequence. Most probably cyclic dependency found in: " + str(
                    remaining_projects))
        next_level_projects = set()
        while True:
            level_changed = False
            remaining_copy = copy.copy(remaining_projects)
            for project in remaining_copy:
                dependencies = project_dependencies[project]
                project_root = project_roots[project]
                dependencies_copy = copy.copy(dependencies)
                for dependency in dependencies_copy:
                    if dependency in current_level:
                        dependencies.remove(dependency)
                    if (not (project in next_level_projects)) and (project_root != project_roots[dependency]):
                        next_level_projects.add(project)
                if not dependencies and not (project in next_level_projects):
                    level_changed = True
                    current_level.append(project)
                    remaining_projects.remove(project)
            if not level_changed:
                break
        levels.append(current_level)
    return levels
def analyze_project_roots(projects):
    project_roots = {}
    for project in projects:
        project_roots[project] = get_project_root_path(project.get_path())
    return project_roots
def get_project_root_path(project_path):
    project_root_path = project_path
    while not is_root(project_root_path):
        parent_path = os.path.dirname(project_root_path)
        parent_pom_path = os.path.join(parent_path, "pom.xml")
        if not os.path.exists(parent_pom_path):
            break
        sub_modules = read_sub_modules(parent_path)
        if not (os.path.basename(project_root_path) in sub_modules):
            break
        project_root_path = parent_path
    return project_root_path
def get_direct_dependencies(project):
    """
    :type project: MavenProject
    """
    pom_path = project.get_pom_path()
    results = find_in_file(pom_path, ["dependencies/dependency"])
    dependencies = results["dependencies/dependency"]
    if isinstance(dependencies, list):
        return dependencies
    elif dependencies is None:
        return []
    else:
        return [dependencies]
def read_sub_modules(module_path):
    pom_path = os.path.join(module_path, 'pom.xml')
    modules_info = find_in_file(pom_path, ["modules/module", "profiles/profile"])
    sub_modules = as_list(modules_info["modules/module"])
    if modules_info["profiles/profile"]:
        profiles = as_list(modules_info["profiles/profile"])
        for profile in profiles:
            profile_modules_info = profile.get("modules")
            if (profile.get("activation")
                and profile.get("activation").get("activeByDefault") == "true"
                and profile_modules_info):
                profile_modules = as_list(profile_modules_info["module"])
                sub_modules.extend(profile_modules)
    return sub_modules
def def_repo_path():
    home = os.path.expanduser("~")
    maven_path = os.path.join(home, ".m2")
    settings_path = os.path.join(maven_path, "settings.xml")
    if os.path.exists(settings_path):
        values = find_in_file(settings_path, ["localRepository"])
        local_repository = values["localRepository"]
        if local_repository is not None:
            local_repository = local_repository.replace("${user.home}", home)
            return local_repository
    return os.path.join(maven_path, "repository")
def requires_archive(project):
    """
    :rtype: MavenProject
    """
    buildable_paths = get_buildable_paths(project)
    for buildable_path in buildable_paths:
        for root, subdirs, files in os.walk(buildable_path):
            if files:
                return True
    return False
def target_build_date(project):
    if not requires_archive(project):
        return datetime.datetime.today()
    target = get_full_build_directory(project)
    target_artifact_path = os.path.join(target, project.get_artifact_name())
    if not os.path.exists(target_artifact_path):
        return None
    return modification_date(target_artifact_path)
def get_buildable_paths(project):
    """
    :type project: MavenProject
    """
    return project.get_source_folders()
def renew_metadata(projects, repo_path):
    if not projects:
        return
    now = datetime.datetime.now()
    current_time = datetime.datetime.strftime(now, "%Y%m%d%H%M%S")
    for project in projects:
        project_repo_path = repo_folder_path(project, repo_path)
        metadata_path = os.path.join(project_repo_path, "maven-metadata-local.xml")
        update_file = os.path.exists(metadata_path)
        if update_file:
            try:
                replace_in_tree(metadata_path, {
                    "versioning/lastUpdated": current_time,
                    "versioning/snapshotVersions/snapshotVersion/updated": current_time
                })
            except xml.etree.ElementTree.ParseError:
                print(project.artifact_id + ' metadata is broken, rewriting')
                update_file = False
        if not update_file:
            local_metadata = '<metadata modelVersion="1.1.0">' + \
                             '  <groupId>' + project.group + '</groupId>' + \
                             '  <artifactId>' + project.artifact_id + '</artifactId>' + \
                             '  <version>' + project.version + '</version>' + \
                             '  <versioning>' + \
                             '    <snapshot>' + \
                             '      <localCopy>true</localCopy>' + \
                             '    </snapshot>' + \
                             '    <lastUpdated>' + current_time + '</lastUpdated>' + \
                             '    <snapshotVersions>' + \
                             '      <snapshotVersion>' + \
                             '        <extension>pom</extension>' + \
                             '        <value>' + project.version + '</value>' + \
                             '        <updated>' + current_time + '</updated>' + \
                             '      </snapshotVersion>'
            if requires_archive(project):
                local_metadata += '      <snapshotVersion>' + \
                                  '        <extension>' + project.get_packaging() + '</extension>' + \
                                  '        <value>' + project.version + '</value>' + \
                                  '        <updated>' + current_time + '</updated>' + \
                                  '      </snapshotVersion>'
            local_metadata += '    </snapshotVersions>' + \
                              '  </versioning>' + \
                              '</metadata>'
            write_file(metadata_path, local_metadata)
def find_module(parent_path, module_name):
    if os.path.exists(os.path.join(parent_path, module_name)):
        return os.path.join(parent_path, module_name)
    for file in os.listdir(parent_path):
        file_path = os.path.join(parent_path, file)
        if not os.path.isdir(file_path):
            continue
        pom_path = os.path.join(file_path, 'pom.xml')
        if not os.path.exists(pom_path):
            continue
        xml_values = find_in_file(pom_path,
                                            ["artifactId"])
        artifact_id = xml_values["artifactId"]
        if artifact_id == module_name:
            return file_path
    return None
def gather_all_poms(root_path, root_only):
    if root_only:
        def gather_children(parent_project_path):
            paths = []
            modules = read_sub_modules(parent_project_path)
            for module in modules:
                module_path = find_module(parent_project_path, module)
                if not (module_path and os.path.exists(module_path)):
                    raise IncorrectConfigException(
                        "Child module '{}' not found. Check parent pom file: {}".format(
                            module,
                            os.path.join(parent_project_path, 'pom.xml')))
                pom_path = os.path.join(module_path, 'pom.xml')
                paths.append(pom_path)
                paths.extend(gather_children(module_path))
            return paths
        result = [os.path.join(root_path, 'pom.xml')]
        result.extend(gather_children(root_path))
        return result
    else:
        result = []
        for root, subdirs, files in os.walk(root_path):
            for file in files:
                if file != 'pom.xml':
                    continue
                pom_path = os.path.join(root, file)
                result.append(pom_path)
        return result
def is_built(project):
    """
    :type project: MavenProject
    """
    if not requires_archive(project):
        return True
    build_directory = get_full_build_directory(project)
    built_artifact_path = os.path.join(build_directory, project.get_artifact_name())
    return os.path.exists(built_artifact_path)
def get_full_build_directory(project):
    build_directory = project.get_build_directory()
    if not os.path.isabs(build_directory):
        build_directory = os.path.join(project.get_path(), build_directory)
    return build_directory
def fast_install(project, repo_path):
    """
    :type project: MavenProject
    :type repo_path: str
    """
    repo_pom = repo_pom_path(project, repo_path)
    prepare_folder(os.path.dirname(repo_pom))
    shutil.copyfile(project.get_pom_path(), repo_pom)
    if requires_archive(project):
        build_directory = get_full_build_directory(project)
        built_artifact_path = os.path.join(build_directory, project.get_artifact_name())
        repo_artifact = repo_artifact_path(project, repo_path)
        if (not os.path.exists(repo_artifact)) \
                or (not equal(built_artifact_path, repo_artifact)):
            shutil.copyfile(built_artifact_path, repo_artifact)
    renew_metadata([project], repo_path)
class IncorrectConfigException(Exception):
    """Raised, when maven configuration or structure is invalid"""
(ROOT_PROJECT_PATH, MAVEN_REPO_PATH, MVN_OPTS, ROOT_ONLY, TRACK_UNVERSIONED, vcs_gateway) = parse_options()
def is_important(file_path):
    return not file_path.endswith(".iml")
def get_unique_name(root_project_path):
    if os.name == 'nt':
        result = root_project_path.replace('\\', "_")
    else:
        result = root_project_path.replace('/', "_")
    result = result.replace(":", "_")
    return result
changed_files = vcs_gateway.get_local_changed_files(ROOT_PROJECT_PATH, not TRACK_UNVERSIONED)
important_files = filter(is_important, changed_files)
pom_paths = set([])
for file_path in important_files:
    file_path = normalize_path(file_path)
    if os.path.isdir(file_path):
        parent_path = file_path
    else:
        parent_path = os.path.dirname(file_path)
    while parent_path and not (is_root(parent_path)):
        pom_path = os.path.join(parent_path, "pom.xml")
        if os.path.exists(pom_path):
            pom_paths.add(pom_path)
            break
        if parent_path == ROOT_PROJECT_PATH:
            break
        parent_path = os.path.dirname(parent_path)
new_in_progress = set(pom_paths)
home_folder = os.path.expanduser('~')
unique_name = get_unique_name(ROOT_PROJECT_PATH)
in_progress_file = os.path.join(home_folder, '.incremaven', unique_name)
prev_in_progress = []
if os.path.exists(in_progress_file):
    prev_in_progress = read_file(in_progress_file).split("\n")
    prev_in_progress = filter(lambda line: line != "", prev_in_progress)
for pom_path in prev_in_progress:
    if os.path.exists(pom_path):
        pom_paths.add(pom_path)
write_file(in_progress_file, "\n".join(pom_paths))
projects = to_mvn_projects(pom_paths, ROOT_PROJECT_PATH, ROOT_ONLY)
to_rebuild = []
to_install = []
for project in projects:
    build_date = target_build_date(project)
    if build_date is None:
        print(str(project) + ' needs rebuild. Artifact is missing in target')
        to_rebuild.append(project)
        continue
    project_src_paths = get_buildable_paths(project)
    src_modification = last_modification(project_src_paths)
    if build_date < src_modification:
        print(str(project) + ' needs rebuild. Last build update: ' + str(build_date))
        to_rebuild.append(project)
    else:
        to_install.append(project)
print('Installing non-changed artifacts to local repository...')
for project in to_install:
    fast_install(project, MAVEN_REPO_PATH)
print('Rebuilding projects...')
rebuild(ROOT_PROJECT_PATH, to_rebuild, MVN_OPTS)
write_file(in_progress_file, '\n'.join(new_in_progress))