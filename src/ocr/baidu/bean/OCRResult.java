package ocr.baidu.bean;

import java.util.List;

public class OCRResult {
    private String log_id;

    private int words_result_num;

    private List<Words_result> words_result ;

    public void setLog_id(String log_id){
        this.log_id = log_id;
    }
    public String getLog_id(){
        return this.log_id;
    }
    public void setWords_result_num(int words_result_num){
        this.words_result_num = words_result_num;
    }
    public int getWords_result_num(){
        return this.words_result_num;
    }
    public void setWords_result(List<Words_result> words_result){
        this.words_result = words_result;
    }
    public List<Words_result> getWords_result(){
        return this.words_result;
    }
}
