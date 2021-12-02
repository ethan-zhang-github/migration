package priv.ethanzhang.migration.example.event;

import org.junit.Test;
import priv.ethanzhang.migration.core.event.*;
import priv.ethanzhang.migration.core.reader.EasyExcelReader;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.utils.GenericUtil;

import java.io.File;

public class MyMigrationEventSubscriber implements MigrationEventSubscriber<MigrationTaskLifecycleEvent> {

    @Override
    public void subscribe(MigrationTaskLifecycleEvent event) {
        System.out.println("...");
    }

    @Test
    public void test() {
        MyMigrationEventSubscriber subscriber = new MyMigrationEventSubscriber();
        LocalMigrationEventDispatcher.INSTANCE.addSubsriber(subscriber);
        LocalMigrationEventDispatcher.INSTANCE.dispatch(new MigrationTaskFailedEvent(null, null, null));
        LocalMigrationEventDispatcher.INSTANCE.dispatch(new MigrationTaskWarnningEvent(null, null, null));

        EasyExcelReader<Integer> reader1 = new EasyExcelReader<Integer>(new File(""), Integer.class) {};

        EasyExcelReader<Integer> reader2 = new EasyExcelReader<>(new File(""), Integer.class);

        System.out.println(GenericUtil.getInterfaceGenericType(reader1.getClass(), MigrationReader.class, 0));

    }

}
