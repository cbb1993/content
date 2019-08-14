package com.huanhong.content.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tuka2401 on 2017/1/19.
 */

public class DateHandler
{
    List<PlanDate> mTotal = new ArrayList<>();
    List<PlanDate> mResult = new ArrayList<>();

    public List<PlanDate> handle(List<PlanDate> planDateList)
    {
        mTotal.clear();
        mResult.clear();
        if (planDateList == null)
        {
            return mTotal;
        }

        //取出各节点时间
        List<Date> dateList = new ArrayList<>();
        for (PlanDate planDate : planDateList)
        {
            dateList.add(planDate.getStart());
            dateList.add(planDate.getEnd());
        }

        if (dateList.size() > 0)
        {
            //去重
            Set<Date> dateSet = new HashSet<>();
            dateSet.addAll(dateList);
            dateList.clear();
            dateList.addAll(dateSet);

            //排序，升序
            Collections.sort(dateList, new Comparator<Date>()
            {
                @Override
                public int compare(Date lhs, Date rhs)
                {
                    return lhs.compareTo(rhs);
                }
            });

            //生成planDate列表
            for (int i = 0; i + 1 < dateList.size(); i++)
            {
                PlanDate date = new PlanDate();
                date.setStart(dateList.get(i));
                date.setEnd(dateList.get(i + 1));
                mTotal.add(date);
            }

            //设置planId
            for (PlanDate src : planDateList)
            {
                Date start = src.getStart();
                Date end = src.getEnd();
                for (PlanDate planDate : mTotal)
                {
                    if (planDate
                            .getStart()
                            .compareTo(start) >= 0
                            && planDate
                            .getEnd()
                            .compareTo(end) <= 0)
                    {
                        planDate.setPlanId(src.getPlanId());
                    }
                }
            }

            //合并
            int index = -1;
            for (int i = 0; i < mTotal.size(); i++)
            {
                PlanDate planDate = mTotal.get(i);
                if (index >= 0)
                {
                    PlanDate last = mResult.get(index);
                    if (planDate.getPlanId() != last.getPlanId())
                    {
                        mResult.add(planDate);
                        index++;
                    } else
                    {
                        last.setEnd(planDate.getEnd());
                    }
                } else
                {
                    mResult.add(planDate);
                    index++;
                }
            }
        }
        return mResult;
    }

    public class PlanDate
    {
        java.util.Date start;
        java.util.Date end;
        Integer planId = -1;

        public Date getStart()
        {
            return start;
        }

        public void setStart(Date start)
        {
            this.start = start;
        }

        public Date getEnd()
        {
            return end;
        }

        public void setEnd(Date end)
        {
            this.end = end;
        }

        public Integer getPlanId()
        {
            return planId;
        }

        public void setPlanId(Integer planId)
        {
            this.planId = planId;
        }
    }
}
