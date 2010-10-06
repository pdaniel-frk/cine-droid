/**
 * Copyright 2010 R King
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cinedroid.tasks;

/**
 * This interface should be implemented by tasks which may be required to be rerun at a later time (for example the http request failed). It
 * is up to the implementor to supply the correct constructor arguments, as well as the params to execute. The {@link #resurrect()} method
 * <b>MUST</b> be called from the UI thread.
 * 
 * @author Kingamajick
 * 
 */
public interface ResurrectableTask {

	/**
	 * Resurrect the task and rerun!
	 */
	public void resurrect();
}
