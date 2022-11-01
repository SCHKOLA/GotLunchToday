/*
 * MIT License
 *
 * Copyright 2017 Niklas Merkelt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.task;

import androidx.core.util.Consumer;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.util.StatsResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatTask implements AsyncTask<StatsResult> {

    private final LunchDatabase database;
    private final Consumer<StatsResult> runnable;

    public StatTask(LunchDatabase database, Consumer<StatsResult> runnable) {
        this.runnable = runnable;
        this.database = database;
    }

    @Override
    public StatsResult doInBackground() {
        int lunchA = database.lunchDao().getLunchCount(1);
        int lunchB = database.lunchDao().getLunchCount(2);
        int lunchS = database.lunchDao().getLunchCount(3);
        int gotLunchA = database.lunchDao().getDispensedLunchCount(1);
        int gotLunchB = database.lunchDao().getDispensedLunchCount(2);
        int gotLunchS = database.lunchDao().getDispensedLunchCount(3);
        ArrayList<String> getLunchA = new ArrayList<>();
        ArrayList<String> getLunchB = new ArrayList<>();
        ArrayList<String> getLunchS = new ArrayList<>();
        List<Customer> array = database.lunchDao().getToDispenseLunch();
        for (Customer c : array) {
            switch (c.lunch) {
                case 1:
                    getLunchA.add(c.name);
                    break;
                case 2:
                    getLunchB.add(c.name);
                    break;
                case 3:
                    getLunchS.add(c.name);
                    break;
            }
        }
        Collections.sort(getLunchA);
        Collections.sort(getLunchB);
        Collections.sort(getLunchS);
        StatsResult result = new StatsResult();
        result.setLunchA(lunchA);
        result.setLunchB(lunchB);
        result.setLunchS(lunchS);
        result.setDispensedA(gotLunchA);
        result.setDispensedB(gotLunchB);
        result.setDispensedS(gotLunchS);
        result.setToDispenseA(getLunchA);
        result.setToDispenseB(getLunchB);
        result.setToDispenseS(getLunchS);
        return result;
    }

    @Override
    public void onPostExecute(StatsResult jsonObject) {
        runnable.accept(jsonObject);
    }
}
