package com.poiji.bind;

import com.poiji.bind.mapping.HSSFPropertyFile;
import com.poiji.bind.mapping.HSSFPropertyStream;
import com.poiji.bind.mapping.PoijiPropertyHelper;
import com.poiji.bind.mapping.UnmarshallerHelper;
import com.poiji.exception.IllegalCastException;
import com.poiji.exception.InvalidExcelFileExtension;
import com.poiji.exception.PoijiExcelType;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import com.poiji.option.PoijiOptions.PoijiOptionsBuilder;
import com.poiji.save.FileSaverFactory;
import com.poiji.util.Files;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.poiji.util.PoijiConstants.CSV_EXTENSION;
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

    private static final Files files = Files.getInstance();

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
        return fromExcelProperties(file, type, PoijiOptionsBuilder.settings().build());
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
    public static <T> T fromExcelProperties(final InputStream inputStream,
        PoijiExcelType excelType,
        final Class<T> type) {
        return fromExcelProperties(inputStream, excelType, type, PoijiOptionsBuilder.settings().build());
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
        HSSFPropertyFile hssfPropertyFile = deserializerPropertyFile(file, options);
        return hssfPropertyFile.unmarshal(type);
    }

    private static HSSFPropertyFile deserializerPropertyFile(final File file, PoijiOptions options) {
        String extension = files.getExtension(file.getName());
        if (XLSX_EXTENSION.equals(extension)) {
            return PoijiPropertyHelper.createPoijiPropertyFile(file, options);
        } else if (XLS_EXTENSION.equals(extension)) {
            throw new InvalidExcelFileExtension("Reading metadata from (" + extension + "), is not supported");
        } else {
            throw new InvalidExcelFileExtension("Invalid file extension (" + extension + "), expected .xlsx");
        }
    }

    private static HSSFPropertyStream deserializerPropertyStream(PoijiExcelType excelType, InputStream inputStream, PoijiOptions options) {
        if (excelType == PoijiExcelType.XLSX) {
            return PoijiPropertyHelper.createPoijiPropertyStream(inputStream, options);
        } else {
            throw new InvalidExcelFileExtension("Reading metadata from (" + excelType + "), is not supported");
        }
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
    public static <T> T fromExcelProperties(final InputStream inputStream,
        PoijiExcelType excelType,
        final Class<T> type,
        PoijiOptions options) {
        Objects.requireNonNull(excelType);
        HSSFPropertyStream hssfPropertyStream = deserializerPropertyStream(excelType, inputStream, options);
        return hssfPropertyStream.unmarshal(type);
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
        final ArrayList<T> list = new ArrayList<>();
        fromExcel(file, type, list::add);
        return list;
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
        final Unmarshaller unmarshaller = deserializer(file, PoijiOptionsBuilder.settings().build());
        unmarshaller.unmarshal(type, consumer);
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
        final ArrayList<T> list = new ArrayList<>();
        fromExcel(inputStream, excelType, type, list::add);
        return list;
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
        Objects.requireNonNull(excelType);

        final Unmarshaller unmarshaller = deserializer(inputStream, excelType, PoijiOptionsBuilder.settings().build());
        unmarshaller.unmarshal(type, consumer);
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
        final ArrayList<T> list = new ArrayList<>();
        fromExcel(file, type, options, list::add);
        return list;
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
        final Unmarshaller unmarshaller = deserializer(file, options);
        unmarshaller.unmarshal(type, consumer);
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
        Objects.requireNonNull(excelType);
        final ArrayList<T> list = new ArrayList<>();
        fromExcel(inputStream, excelType, type, options, list::add);
        return list;
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
        Objects.requireNonNull(excelType);

        final Unmarshaller unmarshaller = deserializer(inputStream, excelType, options);
        unmarshaller.unmarshal(type, consumer);
    }

    public static <T> void toExcel(final File file, final Class<T> clazz, final Collection<T> data) {
        toExcel(file, clazz, data, PoijiOptionsBuilder.settings().build());
    }

    public static <T> void toExcel(
        final File file, final Class<T> clazz, final Collection<T> data, final PoijiOptions options
    ) {
        new FileSaverFactory<>(clazz, options).toFile(file).save(data);
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Collection<T> data
    ) {
        toExcel(outputStream, excelType, clazz, data, PoijiOptionsBuilder.settings().build());
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Collection<T> data, final PoijiOptions options
    ) {
        new FileSaverFactory<>(clazz, options).toOutputStream(outputStream, excelType).save(data);
    }

    public static <T> void toExcel(final File file, final Class<T> clazz, final Stream<T> data) {
        toExcel(file, clazz, data, PoijiOptionsBuilder.settings().build());
    }

    public static <T> void toExcel(
        final File file, final Class<T> clazz, final Stream<T> data, final PoijiOptions options
    ) {
        new FileSaverFactory<>(clazz, options).toFile(file).save(data);
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Stream<T> data
    ) {
        toExcel(outputStream, excelType, clazz, data, PoijiOptionsBuilder.settings().build());
    }

    public static <T> void toExcel(
        final OutputStream outputStream, final PoijiExcelType excelType, final Class<T> clazz, final Stream<T> data, final PoijiOptions options
    ) {
        new FileSaverFactory<>(clazz, options).toOutputStream(outputStream, excelType).save(data);
    }

    private static Unmarshaller deserializer(final File file, final PoijiOptions options) {
        final PoijiFile<?> poijiFile = new PoijiFile<>(file);

        String extension = files.getExtension(file.getName());

        switch (extension) {
            case XLS_EXTENSION:
                return UnmarshallerHelper.HSSFInstance(poijiFile, options);
            case XLSX_EXTENSION:
                return UnmarshallerHelper.XSSFInstance(poijiFile, options);
            case CSV_EXTENSION:
                return UnmarshallerHelper.csvInstance(poijiFile, options);
            default:
                throw new InvalidExcelFileExtension(
                    "Invalid file extension (" + extension + "), excepted .xls or .xlsx or .csv");
        }
    }

    private static Unmarshaller deserializer(final InputStream inputStream, PoijiExcelType excelType, final PoijiOptions options) {
        final PoijiInputStream<?> poijiInputStream = new PoijiInputStream<>(inputStream);

        switch (excelType) {
            case XLS:
                return UnmarshallerHelper.HSSFInstance(poijiInputStream, options);
            case XLSX:
                return UnmarshallerHelper.XSSFInstance(poijiInputStream, options);
            case CSV:
                return UnmarshallerHelper.csvInstance(poijiInputStream, options);
            default:
                throw new InvalidExcelFileExtension(
                    "Invalid file extension (" + excelType + "), excepted .xls or .xlsx or .csv");
        }
    }

}
