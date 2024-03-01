package io.github.heathchen.mybatisplus.util.strategy;

import io.github.heathchen.mybatisplus.util.annotation.QueryField;
import io.github.heathchen.mybatisplus.util.enums.ConditionType;
import io.github.heathchen.mybatisplus.util.enums.QueryType;
import io.github.heathchen.mybatisplus.util.utils.PageHelperUtil;
import io.github.heathchen.mybatisplus.util.utils.QueryParamThreadLocal;
import io.github.heathchen.mybatisplus.util.utils.QueryUtil;
import io.github.heathchen.mybatisplus.util.utils.TableUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.lang.reflect.Field;

/**
 * LIKE '%值%' 查询策略类
 *
 * @author HeathCHEN
 * @version 1.0
 * @since 2024/02/26
 */
public class LikeQueryTypeStrategy implements QueryTypeStrategy {

    private static final QueryType QUERY_TYPE = QueryType.LIKE;

    public LikeQueryTypeStrategy() {
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
        if (ObjectUtil.isNull(value)) {
            return;
        }

        //查询属性名对应字段名
        String tableColumnName = TableUtil.getTableColumnName(clazz, field);

        String[] orColumns = queryField.orColumns();
        if (QueryUtil.checkValue(value)) {
            if (ArrayUtil.isNotEmpty(orColumns)) {
                queryWrapper.and(tQueryWrapper -> {
                            tQueryWrapper.like(tableColumnName, value);
                            for (String orColumn : orColumns) {
                                tQueryWrapper.or();
                                tQueryWrapper.like(TableUtil.checkOrColumnName(orColumn), value);
                            }
                        }
                );
            } else {
                queryWrapper.like(tableColumnName, value);
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
