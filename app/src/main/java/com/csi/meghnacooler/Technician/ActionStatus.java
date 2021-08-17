package com.csi.meghnacooler.Technician;

/**
 * CREATED BY AK IJ
 * 28-12-2020
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public interface ActionStatus {
    class status{
        List<String> doneIdList= new ArrayList<>();
        List<String> pendingIdList= new ArrayList<>();
        List<String> workshopIdList= new ArrayList<>();
        List<String> noProblemIdList= new ArrayList<>();
        List<String> selectList= new ArrayList<>();
        //String select= "Select";
    }
}
