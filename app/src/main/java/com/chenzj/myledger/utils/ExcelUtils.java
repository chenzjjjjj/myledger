package com.chenzj.myledger.utils;

import android.net.Uri;
import android.os.Environment;
import com.chenzj.myledger.model.Ledger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/25 17:31
 */
public class ExcelUtils {
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel
    public static String FILE_PREFIX = "记帐";
    public static String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"";

    public static void setFilePath(String path){
        FILE_PATH = path;
    }

    /**
     * 导出Excel
     * @param listData
     * @return
     */
    public static boolean exportExcel(List<Ledger> listData) {
        try {
            // 创建excel xlsx格式
            Workbook wb = new XSSFWorkbook();
            // 创建工作表
            Sheet sheet = wb.createSheet();
            String[] title = {"日期", "分类", "记账类型", "金额", "备注"};
            //创建行对象
            Row row = sheet.createRow(0);
            // 设置有效数据的行数和列数
            int colNum = title.length;

            for (int i = 0; i < colNum; i++) {
                sheet.setColumnWidth(i, 20 * 256);  // 显示20个字符的宽度
                Cell cell1 = row.createCell(i);
                //第一行
                cell1.setCellValue(title[i]);
            }

            // 导入数据
            for (int rowNum = 0; rowNum < listData.size(); rowNum++) {

                // 之所以rowNum + 1 是因为要设置第二行单元格
                row = sheet.createRow(rowNum + 1);
                // 设置单元格显示宽度
                row.setHeightInPoints(28f);

                // PhonebillExpressBean 这个是我的业务类，这个是根据业务来进行填写数据
                Ledger bean = listData.get(rowNum);

                for (int j = 0; j < title.length; j++) {
                    Cell cell = row.createCell(j);

                    //要和title[]一一对应
                    switch (j) {
                        case 0:
                            //日期
                            cell.setCellValue(bean.getInsertTime());
                            break;
                        case 1:
                            //分类
                            cell.setCellValue(bean.getClassify());
                            break;
                        case 2:
                            //记账类型
                            cell.setCellValue(bean.getType()==0?"支出":"收入");
                            break;
                        case 3:
                            //金额
                            cell.setCellValue(bean.getAmount());
                            break;
                        case 4:
                            //备注
                            cell.setCellValue(bean.getRemark());
                            break;
                    }
                }
            }

            File dir = new File(FILE_PATH);
            //判断文件是否存在
            if (!dir.isFile()) {
                //不存在则创建
                dir.mkdir();
            }
            File excel = new File(dir, FILE_PREFIX+TimeUtils.convertTime(System.currentTimeMillis(), "MM月dd日HH时mm分") + ".xlsx");

            FileOutputStream fos = new FileOutputStream(excel);
            wb.write(fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            Logger.e("ExcelUtils", "exportExcel", e);
            return false;
        }

    }

    public static List<Ledger> importExcel(InputStream inStr, String fileName) {
        List<Ledger> ledgerList = new ArrayList<>();
        try {
            // 读取文件
            Workbook wb = getWorkbook(inStr,fileName);
            Sheet sheet = wb.getSheetAt(0);
            Row row = null;
            Ledger ledger = null;
            //遍历excel文件的每行每列
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                //读取一行
                row = sheet.getRow(j);
                //去掉空行和表头
                if( row==null || row.getFirstCellNum() == j ){ continue; }
                ledger = new Ledger();
                //遍历所有的列
                ledger.setInsertTime(getDateValue(row.getCell(0)));
                ledger.setClassify(row.getCell(1).getStringCellValue());
                ledger.setType(row.getCell(2).getStringCellValue().indexOf("收入") > -1 ? 1 : 0 );
                ledger.setAmount(row.getCell(3).getNumericCellValue());
                ledger.setRemark(row.getCell(4).getStringCellValue());
                ledgerList.add(ledger);
            }
        }catch (Exception e){
            Logger.e("ExcelUtils", "importExcel", e);
        }
        return ledgerList;
    }


    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     */
    public static  Workbook getWorkbook(InputStream inStr, String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    public static String getDateValue(Cell cell){
        String value = cell.getStringCellValue();
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                //如果是date类型则 ，获取该cell的date值
                Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                value = TimeUtils.date2string(date);;
            }
        }
        return value;
    }

    //解决excel类型问题，获得数值
    public static String getValue(Cell cell) {
        String value = "";
        if(null==cell){
            return value;
        }
        switch (cell.getCellType()) {
            //数值型
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    //如果是date类型则 ，获取该cell的date值
                    Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                    value = TimeUtils.date2string(date);;
                }else {// 纯数字
                    BigDecimal big=new BigDecimal(cell.getNumericCellValue());
                    value = big.toString();
                }
                break;
            //字符串类型
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue().toString();
                break;
            // 公式类型
            case Cell.CELL_TYPE_FORMULA:
                //读公式计算值
                value = String.valueOf(cell.getNumericCellValue());
                if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
                    value = cell.getStringCellValue().toString();
                }
                break;
            // 布尔类型
            case Cell.CELL_TYPE_BOOLEAN:
                value = " "+ cell.getBooleanCellValue();
                break;
            default:
                value = cell.getStringCellValue().toString();
        }
        if("null".endsWith(value.trim())){
            value="";
        }
        return value;
    }

}
