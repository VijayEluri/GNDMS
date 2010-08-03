package de.zib.gndms.kit.access;

/*
 * Copyright 2008-2010 Zuse Institute Berlin (ZIB)
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



import org.jetbrains.annotations.NotNull;


/**
 * ThingAMagic.
 *
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 05.12.2008 Time: 17:57:12
 */
public interface InstanceProvider {
	@NotNull <T> T waitForInstance(@NotNull Class<T> clazz, @NotNull String name);

	void addInstance(@NotNull String name, @NotNull Object obj);

	@NotNull <T> T getInstance(@NotNull Class<? extends T> clazz, @NotNull String name);
}
