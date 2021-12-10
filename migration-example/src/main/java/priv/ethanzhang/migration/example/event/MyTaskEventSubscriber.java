package priv.ethanzhang.migration.example.event;

import org.junit.Test;
import priv.ethanzhang.migration.core.event.*;
import priv.ethanzhang.migration.core.reader.EasyExcelReader;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.utils.GenericUtil;

import java.io.File;

public class MyTaskEventSubscriber implements TaskEventSubscriber<TaskTaskLifecycleEvent> {

    @Override
    public void subscribe(TaskTaskLifecycleEvent event) {
        System.out.println("...");
    }

    @Test
    public void test() {
        MyTaskEventSubscriber subscriber = new MyTaskEventSubscriber();
        LocalTaskEventDispatcher.INSTANCE.addSubsriber(subscriber);
        LocalTaskEventDispatcher.INSTANCE.dispatch(new TaskTaskFailedEvent(null, null, null));
        LocalTaskEventDispatcher.INSTANCE.dispatch(new TaskTaskWarnningEvent(null, null, null));

        EasyExcelReader<Integer> reader1 = new EasyExcelReader<Integer>(new File(""), Integer.class) {};

        EasyExcelReader<Integer> reader2 = new EasyExcelReader<>(new File(""), Integer.class);

        System.out.println(GenericUtil.getInterfaceGenericType(reader1.getClass(), MigrationReader.class, 0));

    }

}
