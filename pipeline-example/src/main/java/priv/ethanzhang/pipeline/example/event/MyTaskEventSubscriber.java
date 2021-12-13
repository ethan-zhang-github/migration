package priv.ethanzhang.pipeline.example.event;

import org.junit.Test;
import priv.ethanzhang.pipeline.core.event.*;
import priv.ethanzhang.pipeline.core.event.dispatcher.GuavaTaskEventDispatcher;
import priv.ethanzhang.pipeline.core.event.subscriber.TaskEventSubscriber;
import priv.ethanzhang.pipeline.core.reader.EasyExcelReader;
import priv.ethanzhang.pipeline.core.reader.PipeReader;
import priv.ethanzhang.pipeline.core.utils.GenericUtil;

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
