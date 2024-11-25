type Image = {
    name: string;
    neighbors: string[]
}
type Metadata = {
    imagePath: string;
    neighborCount: number;
    neighborIndex: number[];
    inverseNeighborIndex: number[];
}

type Data = {
    metadata: Metadata;
    images: Image[]
}

const a: Data = {
    "metadata": {
        "imagePath": "src/main/resources/test/small/",
        "neighborCount": 4,
        "neighborIndex": [0, 1, 2, 3], // top: 0, right: 1, down: 2, left:3
        "inverseNeighborIndex": [2, 3, 0, 1] // down , left, up, right
    },
    "images": [
        {
            "name": "0.png",
            "neighbors": ["0", "0", "0", "0"]
        },
        {
            "name": "1.png",
            "neighbors": ["1", "1", "1", "1"]
        },
        {
            "name": "2.png",
            "neighbors": ["1", "1", "0", "1"]
        },
        {
            "name": "3.png",
            "neighbors": ["1", "0", "0", "1"]
        },
        {
            "name": "4.png",
            "neighbors": ["0", "1", "0", "1"]
        },
        {
            "name": "5.png",
            "neighbors": ["1", "1", "1", "1"]
        },

    ]
}

