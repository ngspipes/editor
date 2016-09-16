/*-
 * Copyright (c) 2016, NGSPipes Team <ngspipes@gmail.com>
 * All rights reserved.
 *
 * This file is part of NGSPipes <http://ngspipes.github.io/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package editor.userInterface.controllers;

import components.FXMLFile;
import editor.dataAccess.Uris;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FXMLLoadController implements IInitializable<FXMLLoadController.Data> {

    private static final long WAIT_TIME = 1000;
    private static final TimeUnit WAIT_TIME_UNIT = TimeUnit.MILLISECONDS;



    public static Node mount(FXMLLoadController.Data data) throws ComponentException {
        String fXMLPath = Uris.FXML_LOAD;
        FXMLFile<Node, FXMLLoadController.Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

        fxmlFile.build();

        return fxmlFile.getRoot();
    }



    public static class Data{
        public final BlockingQueue<String> messageQueue;
        public final AtomicBoolean finish;



        public Data(BlockingQueue<String> messageQueue, AtomicBoolean finish) {
            this.messageQueue = messageQueue;
            this.finish = finish;
        }
    }




    @FXML
    private ListView<String> lVMessages;

    private BlockingQueue<String> messageQueue;
    private AtomicBoolean finish;



    @Override
    public void init(Data data) throws ComponentException {
        this.messageQueue = data.messageQueue;
        this.finish = data.finish;

        load();
    }

    private void load(){
        new Thread(() -> {
            while(!finish.get()){
                try {
                    String msg = messageQueue.poll(WAIT_TIME, WAIT_TIME_UNIT);

                    if(msg!=null)
                        Platform.runLater(() -> lVMessages.getItems().add(msg));

                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();
    }

}
