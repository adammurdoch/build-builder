package org.gradle.builds.model

interface HasHeapRequirements : Component {
    val minHeapMegabytes: Int
}
