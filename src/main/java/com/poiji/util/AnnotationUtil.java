package com.poiji.util;

import com.poiji.annotation.ExcelCellName;
import com.poiji.exception.HeaderMissingException;
import com.poiji.option.PoijiOptions;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by hakan on 2.05.2020
 */
public class AnnotationUtil {

    /**
     * Validate that all headers specified via @ExcelCellName annotations are present in the list of header names.
     * <p>
     * Validation is only performed if it is set in the PoijiOptions
     *
     * @throws HeaderMissingException if one or more headers are missing
     */
    public static <T> void validateMandatoryNameColumns(PoijiOptions options,
                                                        Class<T> modelType,
                                                        Collection<String> headerNames) {
        final Collection<String> mandatoryColumnNames = ReflectUtil
            .findRecursivePoijiAnnotations(modelType, ExcelCellName.class)
            .stream()
            .filter(excelCellName -> options.getNamedHeaderMandatory() || excelCellName.mandatory())
            .flatMap(AnnotationUtil::getNames)
            .collect(toList());

        final BiPredicate<String, String> comparator = options.getCaseInsensitive()
            ? String::equalsIgnoreCase
            : String::equals;

        final Set<String> missingHeaders = mandatoryColumnNames
            .stream()
            .filter(mandatoryColumnName -> {
                final String transformed = options.getFormatting().transform(options, mandatoryColumnName);
                return headerNames.stream().noneMatch(title -> comparator.test(transformed, title));
            })
            .collect(toSet());

        if (!missingHeaders.isEmpty()) {
            throw new HeaderMissingException("Some headers are missing in the sheet: " + missingHeaders);
        }
    }

    private static Stream<String> getNames(final ExcelCellName excelCellName) {
        if (excelCellName.columnNameDelimiter().isEmpty()){
            return Stream.of(excelCellName.value());
        } else {
            return Stream.of(excelCellName.value().split(excelCellName.columnNameDelimiter()));
        }
    }
}
