(ns clocking.models.events)

(defn pair-clockins-and-clockouts [events paired-events]
  (if (empty? events)
    paired-events
    (if (= "clock-out" (:type (first events)))
      (pair-clockins-and-clockouts (rest events)
                                   (conj paired-events (vector (first events))))

      (if (= "clock-in" (:type (first events)))
        (do
          (if (= "clock-out" (:type (second events)))
            (pair-clockins-and-clockouts (rest (rest events)) (conj paired-events (vector (first events) (second events))))
            (pair-clockins-and-clockouts (rest events) (conj paired-events (vector  (first events))))))))))
