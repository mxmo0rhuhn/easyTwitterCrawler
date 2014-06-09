/*
* Copyright (c) 2014 Philipp Gamper and Max Schrimpf
*
* This file is part of the easy Twitter crawler project.
* It enables you to specify a custom query and execute it for a while.
*
* It is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* The program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with the easy Twitter Crawler.  If not, see <http://www.gnu.org/licenses/>.
*/

package ch.schrimpf.core;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class to log tweets into a CSV file
 *
 * Created by Max Schrimpf
 */
public class CSVOutput {

    private static final Logger LOG = Logger.getLogger(CSVOutput.class.getName());
    private final CSVWriter writer;

    public CSVOutput(String filename) throws IOException {

        writer = new CSVWriter(new FileWriter(filename, true));

    }

    public void writeResult(List<String> result) {
        try {
            String[] entries;
    private void writeResult(List<String> result) {
        String[] entries;
        entries = new String[result.size() + 1];
        int i = 0;
        for (String element : result) {
            entries[i] = element;
            i++;
        }
        writer.writeNext(entries);
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            LOG.severe("Could not close ouptut file");
        }
    }
}
