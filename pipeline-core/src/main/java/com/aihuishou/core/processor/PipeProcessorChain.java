package com.aihuishou.core.processor;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PipeProcessorChain<I, O> {

    private final LinkedList<PipeProcessorNode<?, ?>> nodes;

    public PipeProcessorChain(PipeProcessorNode<I, O> node) {
        this.nodes = new LinkedList<>(Collections.singleton(node));
    }

    public PipeProcessorChain(Collection<PipeProcessorNode<?, ?>> nodes) {
        this.nodes = new LinkedList<>(nodes);
    }

    public int length() {
        return nodes.size();
    }

    public List<Class<?>> getProcessorTypes() {
        return nodes.stream().map(Object::getClass).collect(Collectors.toList());
    }

}
