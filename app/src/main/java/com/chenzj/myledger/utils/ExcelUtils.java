package com.chenzj.myledger.utils;

import android.os.Environment;
import com.chenzj.myledger.model.Ledger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/25 17:31
 */
public class ExcelUtils {

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

}
