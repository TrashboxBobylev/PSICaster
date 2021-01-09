package com.trashboxbobylev.psicaster.init;

import com.trashboxbobylev.psicaster.block.BlockCaster;
import io.github.phantamanta44.libnine.InitMe;

public class PSICasterInit {
    @InitMe("psicaster")
    public static void init(){
        new BlockCaster();
    }
}
