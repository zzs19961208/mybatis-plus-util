package io.github.heathchen.mybatisplus.util.strategy;

import io.github.heathchen.mybatisplus.util.annotation.QueryField;
import io.github.heathchen.mybatisplus.util.enums.ConditionType;
import io.github.heathchen.mybatisplus.util.enums.QueryType;
import io.github.heathchen.mybatisplus.util.utils.PageHelperUtil;
import io.github.heathchen.mybatisplus.util.utils.QueryParamThreadLocal;
import io.github.heathchen.mybatisplus.util.utils.QueryUtil;
import io.github.heathchen.mybatisplus.util.utils.TableUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.lang.reflect.Field;

/**
 * NOT LIKE '%值%' 查询策略类
 *
 * @author HeathCHEN
 * @version 1.0
 * @since 2024/02/26
 */
public class NotLikeQueryTypeStrategy implements QueryTypeStrategy {
    private static final QueryType QUERY_TYPE = QueryType.NOT_LIKE;

    public NotLikeQueryTypeStrategy() {
        QueryTypeStrategyManager.putQueryTypeStrategyToManager(QUERY_TYPE.getCompareType(), this);
    }

    /**
     * 构造查询
     *
     * @param queryField QueryField注解
     * @param clazz         类
     * @param field         字段
     * @param queryWrapper  查询queryWrapper
     * @author HeathCHEN
     */
    @Override
    public <T> void buildQuery(QueryField queryField, Class clazz, Field field, QueryWrapper<T> queryWrapper, String[] groupIds) {
        String[] groupIdsOnQueryField = queryField.groupId();
        boolean inGroup = Boolean.FALSE;
        if (ArrayUtil.isNotEmpty(groupIds)) {
            for (String groupId : groupIds) {
                if (ArrayUtil.contains(groupIdsOnQueryField,groupId)) {
                    inGroup = Boolean.TRUE;
                }
            }
        }else {
            inGroup = Boolean.TRUE;
        }

        if (!inGroup) {
            QueryParamThreadLocal.removeParamFromQueryParamMap(field.getName());
            return;
        }

        Object value = QueryParamThreadLocal.getValueFromQueryParamMap(field.getName());
        //将属性转为下划线格式
        String tableColumnName = TableUtil.getTableColumnName(clazz, field);

        String[] orColumns = queryField.orColumns();
        if (QueryUtil.checkValue(value)) {
            if (ArrayUtil.isNotEmpty(orColumns)) {
                queryWrapper.and(tQueryWrapper -> {
                            tQueryWrapper.notLike(tableColumnName, value);
                            for (String orColumn : orColumns) {
                                tQueryWrapper.or();
                                tQueryWrapper.notLike(TableUtil.checkOrColumnName(orColumn), value);
                            }
                        }
                );
            } else {
                queryWrapper.notLike(tableColumnName, value);
            }
        }else {
            if (queryField.conditionType().equals(ConditionType.TABLE_COLUMN_IS_NULL)) {
                queryWrapper.isNull(tableColumnName);
            }
            if (queryField.conditionType().equals(ConditionType.TABLE_COLUMN_IS_NOT_NULL)) {
                queryWrapper.isNotNull(tableColumnName);
            }
        }
        QueryParamThreadLocal.removeParamFromQueryParamMap(field.getName());
        //检查是否使用排序
        PageHelperUtil.checkColumnOrderOnField(queryField, clazz, field, tableColumnName);
    }
}
