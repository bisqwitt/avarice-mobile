package com.tools;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {

    public static void main(String[] args) throws Exception {
        TexturePacker.process("assets-raw", "assets", "atlases");
    }

}
