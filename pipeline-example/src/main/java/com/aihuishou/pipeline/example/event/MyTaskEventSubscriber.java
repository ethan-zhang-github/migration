package com.aihuishou.pipeline.example.event;

import com.aihuishou.core.event.TaskEvent;
import com.aihuishou.core.event.TaskFailedEvent;
import com.aihuishou.core.event.TaskWarnningEvent;
import org.junit.Test;
import com.aihuishou.core.event.dispatcher.GuavaTaskEventDispatcher;
import com.aihuishou.core.event.subscriber.TaskEventSubscriber;
import com.aihuishou.core.reader.EasyExcelReader;
import com.aihuishou.core.reader.PipeReader;
import com.aihuishou.core.utils.GenericUtil;

import java.io.File;

public class MyTaskEventSubscriber implements TaskEventSubscriber {

    @Override
    public void subscribe(TaskEvent event) {
        System.out.println("...");
    }

    @Test
    public void test() {
        MyTaskEventSubscriber subscriber = new MyTaskEventSubscriber();
        GuavaTaskEventDispatcher.INSTANCE.addSubsriber(subscriber);
        GuavaTaskEventDispatcher.INSTANCE.dispatch(new TaskFailedEvent(null, null, null));
        GuavaTaskEventDispatcher.INSTANCE.dispatch(new TaskWarnningEvent(null, null, null));

        EasyExcelReader<Integer> reader1 = new EasyExcelReader<Integer>(new File(""), Integer.class) {};

        EasyExcelReader<Integer> reader2 = new EasyExcelReader<>(new File(""), Integer.class);

        System.out.println(GenericUtil.getInterfaceGenericType(reader1.getClass(), PipeReader.class, 0));

    }

}
