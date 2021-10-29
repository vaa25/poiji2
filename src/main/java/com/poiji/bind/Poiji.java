package com.poiji.bind;

import com.poiji.exception.IllegalCastException;
import com.poiji.exception.InvalidExcelFileExtension;
import com.poiji.exception.PoijiExcelType;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.Sheet;

import static com.poiji.util.PoijiConstants.XLSX_EXTENSION;
import static com.poiji.util.PoijiConstants.XLS_EXTENSION;

/**
 * The entry point of the mapping process.
 * <p>
 * Example:
 * <pre>
 * {@literal List<Employee>} employees = Poiji.fromExcel(new File("employees.xls"), Employee.class);
 * employees.size();
 * // 3
 * Employee firstEmployee = employees.get(0);
 * // Employee{employeeId=123923, name='Joe', surname='Doe', age=30, single=true, birthday='4/9/1987'}
 * </pre>
 * <p>
 * Created by hakan on 16/01/2017.
 */
public final class Poiji {

    private Poiji() {
    }

    /**
     * converts excel properties into an object
     *
     * @param file excel file ending with .xlsx.
     * @param type type of the root object.
     * @param <T>  type of the root object.
     * @return the newly created objects
     * @throws PoijiException            if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException      if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcelProperties(File, Class, PoijiOptions)
     */
    public static <T> T fromExcelProperties(final File file, final Class<T> type) {
        return  Poiji.<T>fromExcelProperties().withSource(file).withJavaType(type).get();
    }

    /**
     * converts excel properties into an object
     *
     * @param inputStream excel file stream
     * @param excelType   type of the excel file, xlsx only!
     * @param type        type of the root object.
     * @param <T>         type of the root object.
     * @return the newly created object
     * @throws PoijiException            if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException      if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcelProperties(InputStream, PoijiExcelType, Class, PoijiOptions)
     */
    public static <T> T fromExcelProperties(final InputStream inputStream, PoijiExcelType excelType, final Class<T> type) {
        return Poiji.<T>fromExcelProperties().withSource(inputStream, excelType).withJavaType(type).get();
    }

    /**
     * converts excel properties into an object
     *
     * @param file    excel file ending with .xlsx.
     * @param type    type of the root object.
     * @param <T>     type of the root object.
     * @param options specifies to change the default behaviour of the poiji. In this case, only the password has an effect
     * @return the newly created object
     * @throws PoijiException            if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException      if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcelProperties(File, Class)
     */
    public static <T> T fromExcelProperties(final File file, final Class<T> type, final PoijiOptions options) {
        return Poiji.<T>fromExcelProperties().withSource(file).withJavaType(type).withOptions(options).get();
    }

    /**
     * converts excel properties into an object
     *
     * @param inputStream excel file stream
     * @param excelType   type of the excel file, xlsx only!
     * @param type        type of the root object.
     * @param <T>         type of the root object.
     * @param options     specifies to change the default behaviour of the poiji. In this case, only the password has an effect
     * @return the newly created object
     * @throws PoijiException            if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException      if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcelProperties(InputStream, PoijiExcelType, Class)
     */
    public static <T> T fromExcelProperties(final InputStream inputStream, PoijiExcelType excelType, Class<T> type, PoijiOptions options) {
        return Poiji.<T>fromExcelProperties().withSource(inputStream, excelType).withJavaType(type).withOptions(options).get();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param file
     *         excel file ending with .xls or .xlsx.
     * @param type
     *         type of the root object.
     * @param <T>
     *         type of the root object.
     * @return
     *         the newly created a list of objects
     *
     * @throws PoijiException
     *          if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension
     *          if the specified excel file extension is invalid.
     * @throws IllegalCastException
     *          if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     *
     * @see Poiji#fromExcel(File, Class, PoijiOptions)
     */
    public static <T> List<T> fromExcel(final File file, final Class<T> type) {
        return Poiji.<T>fromExcel().withSource(file).withJavaType(type).toList();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param file excel file ending with .xls or .xlsx.
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @param consumer output retrieves records
     * @throws PoijiException if an internal exception occurs during the mapping
     * process.
     * @throws InvalidExcelFileExtension if the specified excel file extension
     * is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     *
     * @see Poiji#fromExcel(File, Class, PoijiOptions)
     */
    public static <T> void fromExcel(final File file, final Class<T> type, final Consumer<? super T> consumer) {
        Poiji.<T>fromExcel().withSource(file).withJavaType(type).withConsumer(consumer).toConsume();
    }

    /**
     * converts excel rows into a stream of objects
     *
     * @param file excel file ending with .xls or .xlsx.
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @throws PoijiException if an internal exception occurs during the mapping
     * process.
     * @throws InvalidExcelFileExtension if the specified excel file extension
     * is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     *
     * @see Poiji#fromExcel(File, Class, PoijiOptions)
     */
    public static <T> Stream<T> fromExcelToStream(final File file, final Class<T> type) {
        return Poiji.<T>fromExcel().withSource(file).withJavaType(type).toStream();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param inputStream excel file stream
     * @param excelType   type of the excel file, xls or xlsx
     * @param type        type of the root object.
     * @param <T>         type of the root object.
     * @return the newly created a list of objects
     * @throws PoijiException            if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException      if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class, PoijiOptions)
     */
    public static <T> List<T> fromExcel(
        final InputStream inputStream, PoijiExcelType excelType, final Class<T> type
    ) {
        return Poiji.<T>fromExcel().withSource(inputStream, excelType).withJavaType(type).toList();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param inputStream excel file stream
     * @param excelType type of the excel file, xls or xlsx
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @param consumer represents an operation that accepts the type argument
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class, PoijiOptions)
     */
    public static <T> void fromExcel(
        final InputStream inputStream, PoijiExcelType excelType, final Class<T> type, final Consumer<? super T> consumer
    ) {
        Poiji.<T>fromExcel().withSource(inputStream, excelType).withJavaType(type).withConsumer(consumer).toConsume();
    }

    /**
     * converts excel rows into a stream of objects
     *
     * @param inputStream excel file stream
     * @param excelType type of the excel file, xls or xlsx
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class, PoijiOptions)
     */
    public static <T> Stream<T> fromExcelToStream(final InputStream inputStream, PoijiExcelType excelType, final Class<T> type) {
        return Poiji.<T>fromExcel().withSource(inputStream, excelType).withJavaType(type).toStream();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param file    excel file ending with .xls or .xlsx.
     * @param type    type of the root object.
     * @param <T>     type of the root object.
     * @param options specifies to change the default behaviour of the poiji.
     * @return the newly created a list of objects
     * @throws PoijiException            if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException      if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class)
     */
    public static <T> List<T> fromExcel(final File file, final Class<T> type, final PoijiOptions options) {
        return Poiji.<T>fromExcel().withSource(file).withJavaType(type).withOptions(options).toList();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param file excel file ending with .xls or .xlsx.
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @param options specifies to change the default behaviour of the poiji.
     * @param consumer represents an operation that accepts the type argument
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class)
     */
    public static <T> void fromExcel(
        final File file, final Class<T> type, final PoijiOptions options, final Consumer<? super T> consumer
    ) {
        Poiji.<T>fromExcel().withSource(file).withJavaType(type).withOptions(options).withConsumer(consumer).toConsume();
    }

    /**
     * converts excel rows into a stream of objects
     *
     * @param file excel file ending with .xls or .xlsx.
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @param options specifies to change the default behaviour of the poiji.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class)
     */
    public static <T> Stream<T> fromExcelToStream(final File file, final Class<T> type, final PoijiOptions options) {
        return Poiji.<T>fromExcel().withSource(file).withJavaType(type).withOptions(options).toStream();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param inputStream excel file stream
     * @param excelType   type of the excel file, xls or xlsx
     * @param type        type of the root object.
     * @param <T>         type of the root object.
     * @param options     specifies to change the default behaviour of the poiji.
     * @return the newly created a list of objects
     * @throws PoijiException            if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException      if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class)
     */
    public static <T> List<T> fromExcel(
        final InputStream inputStream, final PoijiExcelType excelType, final Class<T> type, final PoijiOptions options
    ) {
        return Poiji.<T>fromExcel().withSource(inputStream, excelType).withJavaType(type).withOptions(options).toList();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param inputStream excel file stream
     * @param excelType type of the excel file, xls or xlsx
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @param options specifies to change the default behaviour of the poiji.
     * @param consumer represents an operation that accepts the type argument
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java
     * language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class)
     */
    public static  <T> void fromExcel(final InputStream inputStream, final PoijiExcelType excelType,
        final Class<T> type, final PoijiOptions options, final Consumer<? super T> consumer
    ) {
        Poiji.<T>fromExcel().withSource(inputStream, excelType).withJavaType(type).withOptions(options).withConsumer(consumer).toConsume();
    }

    /**
     * converts excel rows into a stream of objects
     *
     * @param inputStream excel file stream
     * @param excelType type of the excel file, xls or xlsx
     * @param type type of the root object.
     * @param <T> type of the root object.
     * @param options specifies to change the default behaviour of the poiji.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension if the specified excel file extension is invalid.
     * @throws IllegalCastException if this Field object is enforcing Java
     * language access control and the underlying field is either inaccessible or final.
     * @see Poiji#fromExcel(File, Class)
     */
    public static <T> Stream<T> fromExcelToStream(
        final InputStream inputStream, final PoijiExcelType excelType, final Class<T> type, final PoijiOptions options
    ) {
        return Poiji.<T>fromExcel().withSource(inputStream, excelType).withJavaType(type).withOptions(options).toStream();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param sheet   excel sheet its workbook must be either  an instance of {@code HSSFWorkbook} or {@code XSSFWorkbook}.
     * @param type    type of the root object.
     * @param <T>     type of the root object.
     * @param options specifies to change the default behaviour of the poiji.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @see Poiji#fromExcel(Sheet, Class, PoijiOptions, Consumer)
     * @see Poiji#fromExcel(Sheet, Class)
     */
    public static <T> List<T> fromExcel(final Sheet sheet, final Class<T> type, final PoijiOptions options) {
        return Poiji.<T>fromExcel().withSource(sheet).withJavaType(type).withOptions(options).toList();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param sheet excel sheet its workbook must be either an instance of {@code HSSFWorkbook} or {@code XSSFWorkbook}.
     * @param type  type of the root object.
     * @param <T>   type of the root object.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @see Poiji#fromExcel(Sheet, Class, PoijiOptions)
     * @see Poiji#fromExcel(Sheet, Class, PoijiOptions, Consumer)
     */
    public static <T> List<T> fromExcel(final Sheet sheet, final Class<T> type) {
        return Poiji.<T>fromExcel().withSource(sheet).withJavaType(type).toList();
    }

    /**
     * converts excel rows into a stream of objects
     *
     * @param sheet excel sheet its workbook must be either an instance of {@code HSSFWorkbook} or {@code XSSFWorkbook}.
     * @param type  type of the root object.
     * @param <T>   type of the root object.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @see Poiji#fromExcel(Sheet, Class, PoijiOptions)
     * @see Poiji#fromExcel(Sheet, Class, PoijiOptions, Consumer)
     */
    public static <T> Stream<T> fromExcelToStream(final Sheet sheet, final Class<T> type) {
        return Poiji.<T>fromExcel().withSource(sheet).withJavaType(type).toStream();
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param sheet    excel sheet its workbook must be either an instance of {@code HSSFWorkbook} or {@code XSSFWorkbook}.
     * @param type     type of the root object.
     * @param <T>      type of the root object.
     * @param options  specifies to change the default behaviour of the poiji.
     * @param consumer represents an operation that accepts the type argument.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @see Poiji#fromExcel(Sheet, Class, PoijiOptions)
     * @see Poiji#fromExcel(Sheet, Class)
     */
    public static <T> void fromExcel(
        final Sheet sheet, final Class<T> type, final PoijiOptions options, final Consumer<? super T> consumer
    ) {
        Poiji.<T>fromExcel().withSource(sheet).withJavaType(type).withOptions(options).withConsumer(consumer).toConsume();
    }

    /**
     * converts excel rows into a stream of objects
     *
     * @param sheet    excel sheet its workbook must be either an instance of {@code HSSFWorkbook} or {@code XSSFWorkbook}.
     * @param type     type of the root object.
     * @param <T>      type of the root object.
     * @param options  specifies to change the default behaviour of the poiji.
     * @throws PoijiException if an internal exception occurs during the mapping process.
     * @see Poiji#fromExcel(Sheet, Class, PoijiOptions)
     * @see Poiji#fromExcel(Sheet, Class)
     */
    public static <T> Stream<T> fromExcelToStream(
        final Sheet sheet, final Class<T> type, final PoijiOptions options
    ) {
        return Poiji.<T>fromExcel().withSource(sheet).withJavaType(type).withOptions(options).toStream();
    }

    public static <T> void toExcel(final File file, final Class<T> clazz, final Collection<T> data) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(file).save();
    }

    public static <T> void toExcel(
        final File file, final Class<T> clazz, final Collection<T> data, final PoijiOptions options
    ) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(file).withOptions(options).save();
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Collection<T> data
    ) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(outputStream, excelType).save();
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Collection<T> data, final PoijiOptions options
    ) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(outputStream, excelType).withOptions(options).save();
    }

    public static <T> void toExcel(final File file, final Class<T> clazz, final Stream<T> data) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(file).save();
    }

    public static <T> void toExcel(
        final File file, final Class<T> clazz, final Stream<T> data, final PoijiOptions options
    ) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(file).withOptions(options).save();
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Stream<T> data
    ) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(outputStream, excelType).save();
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Stream<T> data, final PoijiOptions options
    ) {
        Poiji.<T>toExcel().withJavaType(clazz).withSource(data).withDestination(outputStream, excelType).withOptions(options).save();
    }

    public static <T> ToExcel<T> toExcel(){
        return new ToExcel<>();
    }

    public static <T> FromExcel<T> fromExcel(){
        return new FromExcel<>();
    }

    public static <T> FromExcelProperties<T> fromExcelProperties(){
        return new FromExcelProperties<>();
    }

}
