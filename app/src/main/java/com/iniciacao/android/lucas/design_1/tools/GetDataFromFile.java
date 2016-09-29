package com.iniciacao.android.lucas.design_1.tools;

import android.content.Context;


/**
 * Created by lucas on 22/07/16.
 */
public class GetDataFromFile {

    private Context mContext;

    private IO_file io_file;

    public GetDataFromFile(Context context){
        this.mContext = context;
        io_file = new IO_file(mContext);
    }

    public String getData(String KEY){

        String data = "";

        String[] infos = null;

        String info = io_file.recuperar(IO_file.FILE_INFORMACAO);

        if(!info.equals("")) {

            infos = info.split("\n");
        }

        if(KEY == "tel"){
            assert  infos != null;
            data = infos[1];
        }

        if (KEY == "telSeg"){

            assert infos != null;
            if (!info.isEmpty()) data = infos[3];
        }

        if (KEY == "nome"){
            assert infos != null;
            data = infos[0];
        }

        if (KEY == "password"){
            assert infos != null;
            data = infos[2];
        }

        return data;
    }

}
