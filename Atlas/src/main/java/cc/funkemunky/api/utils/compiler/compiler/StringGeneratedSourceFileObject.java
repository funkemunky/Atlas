/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.funkemunky.api.utils.compiler.compiler;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public final class StringGeneratedSourceFileObject extends SimpleJavaFileObject {

    private final String javaSource;

    public StringGeneratedSourceFileObject(String fullClassName, String javaSource) {
        super(URI.create("string:///" + fullClassName.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.javaSource = javaSource;
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
        return javaSource;
    }

}
